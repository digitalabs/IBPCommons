/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.hibernate;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.SessionFactory;

public class DefaultManagerFactoryProvider extends ManagerFactoryBase implements ManagerFactoryProvider, HttpRequestAware {

	private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();

	private final Map<HttpServletRequest, HibernateSessionProvider> sessionProviders =
			new HashMap<HttpServletRequest, HibernateSessionProvider>();

	public DefaultManagerFactoryProvider() {

	}

	@Override
	public synchronized ManagerFactory getManagerFactoryForProject(Project project) {
		SessionFactory sessionFactory = this.sessionFactoryCache.get(project.getProjectId());
		String databaseName = null;
		if (sessionFactory != null) {
			this.projectAccessList.remove(project.getProjectId());
		}

		databaseName = project.getDatabaseName();

		if (sessionFactory == null || sessionFactory.isClosed()) {
			// close any excess cached session factory
			this.closeExcessSessionFactory();

			DatabaseConnectionParameters params =
					new DatabaseConnectionParameters(this.dbHost, String.valueOf(this.dbPort), databaseName, this.dbUsername,
							this.dbPassword);
			try {
				sessionFactory = SessionFactoryUtil.openSessionFactory(params);
				this.sessionFactoryCache.put(project.getProjectId(), sessionFactory);
			} catch (FileNotFoundException e) {
				throw new ConfigException("Cannot create a SessionFactory for " + project, e);
			}
		}

		// add this session factory to the head of the access list
		this.projectAccessList.add(0, project.getProjectId());

		// get or create the HibernateSessionProvider for the current request
		HttpServletRequest request = DefaultManagerFactoryProvider.CURRENT_REQUEST.get();
		HibernateSessionProvider sessionProvider = this.sessionProviders.get(request);
		if (sessionProvider == null && sessionFactory != null) {
			sessionProvider = new HibernateSessionPerRequestProvider(sessionFactory);
			this.sessionProviders.put(request, sessionProvider);
		}

		ManagerFactory factory = new ManagerFactory();
		factory.setSessionProvider(sessionProvider);
		factory.setDatabaseName(databaseName);
		return factory;
	}

	@Override
	public void onRequestStarted(HttpServletRequest request, HttpServletResponse response) {
		// remember the HttpServletRequest for this thread
		DefaultManagerFactoryProvider.CURRENT_REQUEST.set(request);
	}

	@Override
	public void onRequestEnded(HttpServletRequest request, HttpServletResponse response) {
		HibernateSessionProvider sessionProvider = this.sessionProviders.get(request);
		if (sessionProvider != null) {
			sessionProvider.close();
			this.sessionProviders.remove(request);
		}
		DefaultManagerFactoryProvider.CURRENT_REQUEST.remove();
	}

	@Override
	public synchronized void close() {
		for (HttpServletRequest request : this.sessionProviders.keySet()) {
			HibernateSessionProvider provider = this.sessionProviders.get(request);
			if (provider != null) {
				provider.close();
			}
		}

		for (Long projectId : this.sessionFactoryCache.keySet()) {
			SessionFactory factory = this.sessionFactoryCache.get(projectId);
			if (factory != null) {
				factory.close();
			}
		}
		this.sessionFactoryCache.clear();
		this.sessionProviders.clear();
	}

	public void removeProjectFromSessionCache(long projectId) {
		if (this.sessionFactoryCache != null && this.sessionFactoryCache.containsKey(projectId)) {
			this.sessionFactoryCache.get(projectId).close();
			this.sessionFactoryCache.remove(projectId);
		}
	}
}
