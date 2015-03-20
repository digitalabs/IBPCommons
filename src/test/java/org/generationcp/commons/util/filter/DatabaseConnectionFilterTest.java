package org.generationcp.commons.util.filter;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.support.servlet.MiddlewareServletContextListener;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 3/20/2015
 * Time: 3:31 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class DatabaseConnectionFilterTest {

	public static final String DUMMY_PROJECT_DATABASE_NAME = "projectDbName";

	@Mock
	private FilterConfig config;

	@Mock
	private ServletContext context;

	@Mock
	private ServletRequest servletRequest;

	@Mock
	private ServletResponse response;

	@InjectMocks
	private DatabaseConnectionFilter dut = spy(new DatabaseConnectionFilter());

	@Before
	public void setUp() throws Exception {
		when(config.getServletContext()).thenReturn(context);

	}

	@Test(expected = ServletException.class)
	public void testFilterInitializationIOExceptionOccured() throws IOException, ServletException {
		doThrow(IOException.class).when(dut).getConfigFileInputStream();

		dut.init(config);
	}

	@Test
	public void testFilterInitialization() throws IOException, ServletException {
		doReturn(ResourceFinder.locateFile("test.properties").openStream()).when(dut)
				.getConfigFileInputStream();

		dut.init(config);

		assertNotNull(dut.getDbHost());
		assertNotNull(dut.getDbPassword());
		assertNotNull(dut.getDbUsername());
		assertNotNull(dut.getDbPort());
	}

	@Test
	public void testConstructWorkbenchDataManager() {
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Session session = mock(Session.class);
		when(context.getAttribute(
				MiddlewareServletContextListener.ATTR_WORKBENCH_SESSION_FACTORY))
				.thenReturn(sessionFactory);
		when(sessionFactory.openSession()).thenReturn(session);

		WorkbenchDataManagerImpl
				dataManager = (WorkbenchDataManagerImpl) dut.constructWorkbenchDataManager();

		Session openedSession = (Session) dataManager.getCurrentSession();
		assertEquals(session, openedSession);

	}

	@Test
	public void testRetrieveCurrentProjectSessionFactoryNothingInMap() throws
			IOException {
		dut.setSessionFactoryMap(new HashMap<Long, SessionFactory>());
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Project project = mock(Project.class);
		when(project.getProjectId()).thenReturn((long) 1);
		when(project.getDatabaseName()).thenReturn(DUMMY_PROJECT_DATABASE_NAME);
		doReturn(sessionFactory).when(dut).openSessionFactory(any(DatabaseConnectionParameters.class));

		SessionFactory retrievedFactory = dut.retrieveCurrentProjectSessionFactory(project);

		assertEquals(sessionFactory, retrievedFactory);

		ArgumentCaptor<DatabaseConnectionParameters> argumentCaptor = ArgumentCaptor.forClass(DatabaseConnectionParameters.class);
		verify(dut).openSessionFactory(argumentCaptor.capture());
		assertEquals(DUMMY_PROJECT_DATABASE_NAME, argumentCaptor.getValue().getDbName());

		assertEquals(sessionFactory, dut.getSessionFactoryMap().get(project.getProjectId()));

	}

	@Test
	public void testRetrieveCurrentProjectSessionFactoryAlreadyInMap() throws IOException {
		Map<Long, SessionFactory> sessionFactoryMap = new HashMap<>();
		dut.setSessionFactoryMap(sessionFactoryMap);
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Project project = mock(Project.class);
		when(project.getProjectId()).thenReturn((long) 1);
		when(project.getDatabaseName()).thenReturn(DUMMY_PROJECT_DATABASE_NAME);

		sessionFactoryMap.put(project.getProjectId(), sessionFactory);

		assertEquals(sessionFactory, dut.retrieveCurrentProjectSessionFactory(project));
		verify(dut, never()).openSessionFactory(any(DatabaseConnectionParameters.class));
	}

	@Test
	public void testDoFilter() throws MiddlewareQueryException, ServletException, IOException{
		WorkbenchDataManager workbenchDataManager = mock(WorkbenchDataManager.class);
		Project project = mock(Project.class);
		when(project.getProjectId()).thenReturn((long) 1);
		when(project.getDatabaseName()).thenReturn(DUMMY_PROJECT_DATABASE_NAME);
		doReturn(workbenchDataManager).when(dut).constructWorkbenchDataManager();
		doReturn(project).when(dut).getCurrentProject(workbenchDataManager, servletRequest);
		SessionFactory sessionFactory = mock(SessionFactory.class);

		Map<Long, SessionFactory> sessionFactoryMap = new HashMap<>();
				dut.setSessionFactoryMap(sessionFactoryMap);
		sessionFactoryMap.put(project.getProjectId(), sessionFactory);

		dut.doFilter(servletRequest, response, mock(FilterChain.class));

		verify(servletRequest).setAttribute(DatabaseConnectionFilter.WORKBENCH_DATA_MANAGER, workbenchDataManager);
		verify(servletRequest).setAttribute(eq(DatabaseConnectionFilter.ATTR_MANAGER_FACTORY), any(ManagerFactory.class));
	}

	@Test
	public void testDestroy() {
		SessionFactory sessionFactory = mock(SessionFactory.class);
		when(sessionFactory.isClosed()).thenReturn(false);
		Map<Long, SessionFactory> sessionFactoryMap = new HashMap<>();
		dut.setSessionFactoryMap(sessionFactoryMap);
		sessionFactoryMap.put((long) 1, sessionFactory);

		dut.destroy();
		verify(sessionFactory).close();
	}

	@Test
		public void testDestroyAlreadyClosed() {
			SessionFactory sessionFactory = mock(SessionFactory.class);
			when(sessionFactory.isClosed()).thenReturn(true);
			Map<Long, SessionFactory> sessionFactoryMap = new HashMap<>();
			dut.setSessionFactoryMap(sessionFactoryMap);
			sessionFactoryMap.put((long) 1, sessionFactory);

			dut.destroy();
			verify(sessionFactory, never()).close();
		}

}
