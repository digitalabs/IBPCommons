package org.generationcp.commons.util.filter;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.support.servlet.MiddlewareServletContextListener;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
public class DatabaseConnectionFilter implements Filter {

	private final static Logger LOG = LoggerFactory
				.getLogger(DatabaseConnectionFilter.class);

	public static final String ATTR_MANAGER_FACTORY = "managerFactory";
	public static final String WORKBENCH_DATA_MANAGER = "workbenchDataManager";
	public static final String PARAM_MIDDLEWARE_RESOURCE_FILES = "middleware_additional_resources";

	private FilterConfig filterConfig;
	private String dbHost;
	private String dbPort;
	private String dbUsername;
	private String dbPassword;
	private Map<Long, SessionFactory> sessionFactoryMap;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

		Properties props = new Properties();
		try {
			props.load(getConfigFileInputStream());
			dbHost = props.getProperty("workbench.host");
			dbPort = props.getProperty("workbench.port");
			dbUsername = props.getProperty("workbench.username");
			dbPassword = props.getProperty("workbench.password");
		} catch (IOException e) {
			throw new ServletException(e);
		}

		sessionFactoryMap = new HashMap<>();
	}

	protected InputStream getConfigFileInputStream() throws IOException{
		String databasePropertyFile =
						filterConfig.getServletContext().getInitParameter(
								MiddlewareServletContextListener.PARAM_DATABASE_PROPERTY_FILE);
		return ResourceFinder.locateFile(databasePropertyFile).openStream();
	}

	protected WorkbenchDataManager constructWorkbenchDataManager() {
		ServletContext context = filterConfig.getServletContext();
		SessionFactory workbenchSessionFactory = (SessionFactory) context.getAttribute(
				MiddlewareServletContextListener.ATTR_WORKBENCH_SESSION_FACTORY);

		HibernateSessionProvider sessionProviderForWorkbench = new HibernateSessionPerRequestProvider(
				workbenchSessionFactory);

		return new WorkbenchDataManagerImpl(
				sessionProviderForWorkbench);
	}

	protected SessionFactory retrieveCurrentProjectSessionFactory(Project project, String[] additionalResourceFiles) throws IOException{

		SessionFactory sessionFactory = sessionFactoryMap.get(project.getProjectId());
		if (sessionFactory == null) {
			String databaseName = project.getDatabaseName();
			DatabaseConnectionParameters params = new DatabaseConnectionParameters(
					dbHost, dbPort, databaseName, dbUsername, dbPassword);

			sessionFactory = openSessionFactory(params, additionalResourceFiles);
			sessionFactoryMap.put(project.getProjectId(), sessionFactory);
		}

		return sessionFactory;
	}

	// wrapper method around static call to ContextUtil to make it simpler to test
	protected Project getCurrentProject(WorkbenchDataManager workbenchDataManager, ServletRequest servletRequest) throws MiddlewareQueryException{
		return ContextUtil
				.getProjectInContext(workbenchDataManager,
						(HttpServletRequest) servletRequest);
	}

	// wrapper method around static call to ContextUtil to make it simpler to test
	protected SessionFactory openSessionFactory(DatabaseConnectionParameters params, String[] additionalResourceFiles) throws
			FileNotFoundException {
		return SessionFactoryUtil.openSessionFactory(null, params, additionalResourceFiles);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {

		WorkbenchDataManager workbenchDataManager = constructWorkbenchDataManager();

		servletRequest.setAttribute(WORKBENCH_DATA_MANAGER, workbenchDataManager);
		ManagerFactory factory = null;

		try {
			Project project = getCurrentProject(workbenchDataManager, servletRequest);

			String paramResourceFile = filterConfig.getServletContext().getInitParameter(
					PARAM_MIDDLEWARE_RESOURCE_FILES);

			String[] additionalResourceFiles = null;
			if (paramResourceFile != null && (!paramResourceFile.isEmpty())) {
				additionalResourceFiles = paramResourceFile.split(",");
			}

			SessionFactory sessionFactory = retrieveCurrentProjectSessionFactory(project, additionalResourceFiles);

			assert sessionFactory != null;

			HibernateSessionPerThreadProvider sessionProvider = new HibernateSessionPerThreadProvider(sessionFactory);

			// create a ManagerFactory and set the HibernateSessionProviders
			// we don't need to set the SessionFactories here
			// since we want to a Session Per Request
			factory = new ManagerFactory();
			factory.setSessionProvider(sessionProvider);
			factory.setDatabaseName(project.getDatabaseName());

			servletRequest.setAttribute(ATTR_MANAGER_FACTORY, factory);

			filterChain.doFilter(servletRequest,servletResponse);

		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (factory != null) {
				factory.close();
			}
		}

	}

	@Override
	public void destroy() {
		for (SessionFactory sessionFactory : sessionFactoryMap.values()) {
			if (!sessionFactory.isClosed()) {
				sessionFactory.close();
			}
		}
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public String getDbHost() {
		return dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public Map<Long, SessionFactory> getSessionFactoryMap() {
		return sessionFactoryMap;
	}

	public void setSessionFactoryMap(Map<Long, SessionFactory> sessionFactoryMap) {
		this.sessionFactoryMap = sessionFactoryMap;
	}
}