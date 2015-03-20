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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
public class DatabaseConnectionFilter implements Filter {

	public static final String ATTR_MANAGER_FACTORY = "managerFactory";
	public static final String WORKBENCH_DATA_MANAGER = "workbenchDataManager";

	private FilterConfig filterConfig;
	private String dbHost;
	private String dbPort;
	private String dbUsername;
	private String dbPassword;
	private HibernateSessionPerThreadProvider sessionProvider;
	private Map<Long, SessionFactory> sessionFactoryMap;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		String databasePropertyFile =
				filterConfig.getServletContext().getInitParameter(MiddlewareServletContextListener.PARAM_DATABASE_PROPERTY_FILE);
		Properties props = new Properties();
		try {
			props.load(ResourceFinder.locateFile(databasePropertyFile).openStream());
			dbHost = props.getProperty("workbench.host");
			dbPort = props.getProperty("workbench.port");
			dbUsername = props.getProperty("workbench.username");
			dbPassword = props.getProperty("workbench.password");
		} catch (IOException e) {
			throw new ServletException(e);
		}

		sessionFactoryMap = new HashMap<>();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {

		ServletContext context = filterConfig.getServletContext();
		SessionFactory workbenchSessionFactory = (SessionFactory) context.getAttribute(
				MiddlewareServletContextListener.ATTR_WORKBENCH_SESSION_FACTORY);

		HibernateSessionProvider sessionProviderForWorkbench = new HibernateSessionPerRequestProvider(
				workbenchSessionFactory);

		WorkbenchDataManager workbenchDataManager = new WorkbenchDataManagerImpl(
				sessionProviderForWorkbench);

		servletRequest.setAttribute(WORKBENCH_DATA_MANAGER, workbenchDataManager);
		ManagerFactory factory = null;

		try {
			Project project = ContextUtil
					.getProjectInContext(workbenchDataManager, (HttpServletRequest) servletRequest);

			SessionFactory sessionFactory = sessionFactoryMap.get(project.getProjectId());
			String databaseName = project.getDatabaseName();
			if (sessionFactory == null) {


				DatabaseConnectionParameters params = new DatabaseConnectionParameters(
						dbHost, String.valueOf(dbPort), databaseName, dbUsername, dbPassword);

				sessionFactory = SessionFactoryUtil.openSessionFactory(params);
				sessionFactoryMap.put(project.getProjectId(), sessionFactory);
			}

			if (sessionProvider == null && sessionFactory != null) {
				sessionProvider = new HibernateSessionPerThreadProvider(sessionFactory);
			} else {
				sessionProvider.setSessionFactory(sessionFactory);
			}

			// create a ManagerFactory and set the HibernateSessionProviders
			// we don't need to set the SessionFactories here
			// since we want to a Session Per Request
			factory = new ManagerFactory();
			factory.setSessionProvider(sessionProvider);
			factory.setDatabaseName(databaseName);

			servletRequest.setAttribute(ATTR_MANAGER_FACTORY, factory);

			filterChain.doFilter(servletRequest,servletResponse);

		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}

		if (factory != null) {
			factory.close();
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
}
