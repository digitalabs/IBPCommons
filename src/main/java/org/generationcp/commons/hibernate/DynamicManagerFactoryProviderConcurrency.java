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
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicManagerFactoryProviderConcurrency extends ManagerFactoryBase implements ManagerFactoryProvider, HttpRequestAware {

	private final static Logger LOG = LoggerFactory.getLogger(DynamicManagerFactoryProviderConcurrency.class);

	public DynamicManagerFactoryProviderConcurrency() {
	}

	public DynamicManagerFactoryProviderConcurrency(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	private HibernateSessionPerThreadProvider sessionProvider;

	private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();

	private WorkbenchDataManager workbenchDataManager;

	public synchronized ManagerFactory createInstance() throws MiddlewareQueryException {
		String databaseName = null;

		Project project =
				ContextUtil.getProjectInContext(this.workbenchDataManager, DynamicManagerFactoryProviderConcurrency.CURRENT_REQUEST.get());
		SessionFactory sessionFactory = this.sessionFactoryCache.get(project.getProjectId());
		if (sessionFactory != null) {
			this.projectAccessList.remove(project.getProjectId());
		}

		if (sessionFactory == null || sessionFactory.isClosed()) {
			databaseName = project.getDatabaseName();

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
		} else {
			databaseName = project.getDatabaseName();
		}

		// add this session factory to the head of the access list
		this.projectAccessList.add(0, project.getProjectId());

		if (this.sessionProvider == null && sessionFactory != null) {
			this.sessionProvider = new HibernateSessionPerThreadProvider(sessionFactory);
		} else {
			this.sessionProvider.setSessionFactory(sessionFactory);
		}

		// create a ManagerFactory and set the HibernateSessionProviders
		// we don't need to set the SessionFactories here
		// since we want to a Session Per Request
		ManagerFactory factory = new ManagerFactory();
		factory.setSessionProvider(this.sessionProvider);
		factory.setDatabaseName(databaseName);
		factory.setCropName(project.getCropType().getCropName());
		factory.setPedigreeProfile(this.pedigreeProfile);

		return factory;
	}

	@Override
	public void onRequestStarted(HttpServletRequest request, HttpServletResponse response) {
		DynamicManagerFactoryProviderConcurrency.CURRENT_REQUEST.set(request);
	}

	@Override
	public void onRequestEnded(HttpServletRequest request, HttpServletResponse response) {
		DynamicManagerFactoryProviderConcurrency.CURRENT_REQUEST.remove();
	}

	@Override
	public ManagerFactory getManagerFactoryForProject(Project project) {
		return null;
	}

	@Override
	public void close() {
		if (this.sessionProvider != null) {
			this.sessionProvider.close();
		}
	}

	protected synchronized void closeAllSessionFactories() {
		for (Entry<Long, SessionFactory> entry : this.sessionFactoryCache.entrySet()) {
			entry.getValue().close();
			this.sessionFactoryCache.remove(entry);
		}
	}
}
