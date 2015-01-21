/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
	
    final static Logger LOG = LoggerFactory.getLogger(DynamicManagerFactoryProviderConcurrency.class);

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
    	
    	Project project = ContextUtil.getProjectInContext(workbenchDataManager, CURRENT_REQUEST.get());    	
        SessionFactory sessionFactory = sessionFactoryCache.get(project.getProjectId());       
        if (sessionFactory != null) {
            projectAccessList.remove(project.getProjectId());
        }
        
        if (sessionFactory == null || sessionFactory.isClosed()) {
            databaseName = project.getDatabaseName();
            
            // close any excess cached session factory
            closeExcessSessionFactory();
            
            DatabaseConnectionParameters params = new DatabaseConnectionParameters(
                    dbHost, String.valueOf(dbPort), databaseName, dbUsername, dbPassword);
            try {
                sessionFactory = SessionFactoryUtil.openSessionFactory(params);
                sessionFactoryCache.put(project.getProjectId(), sessionFactory);
            }
            catch (FileNotFoundException e) {
                throw new ConfigException("Cannot create a SessionFactory for " + project, e);
            }
        } else {
        	databaseName = project.getDatabaseName();
        }
        
        // add this session factory to the head of the access list
        projectAccessList.add(0, project.getProjectId());
        
        if (sessionProvider == null && sessionFactory != null) {
            sessionProvider = new HibernateSessionPerThreadProvider(sessionFactory);
        } else {
        	sessionProvider.setSessionFactory(sessionFactory);
        }
        
        // create a ManagerFactory and set the HibernateSessionProviders
        // we don't need to set the SessionFactories here
        // since we want to a Session Per Request 
        ManagerFactory factory = new ManagerFactory();
        factory.setSessionProvider(sessionProvider);
        factory.setDatabaseName(databaseName);
        
        return factory;
    }


	@Override
	public void onRequestStarted(HttpServletRequest request, HttpServletResponse response) {
		CURRENT_REQUEST.set(request);
	}

	@Override
	public void onRequestEnded(HttpServletRequest request, HttpServletResponse response) {
		CURRENT_REQUEST.remove();
	}

	@Override
	public ManagerFactory getManagerFactoryForProject(Project project) {
		return null;
	}

	@Override
	public void close() {
		if (sessionProvider != null) {
			sessionProvider.close();
		}
	}
	
	protected synchronized void closeAllSessionFactories() {
		for (Entry<Long, SessionFactory> entry : sessionFactoryCache.entrySet()){
			entry.getValue().close();
			sessionFactoryCache.remove(entry);
		}
    }   
}
