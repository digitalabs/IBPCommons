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
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.SessionFactory;

public class DynamicManagerFactoryProvider extends ManagerFactoryBase implements ManagerFactoryProvider, HttpRequestAware {
	
	public DynamicManagerFactoryProvider() {
	}
	
	public DynamicManagerFactoryProvider(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
	
	private HibernateSessionPerRequestProvider sessionProvider; 
    
    private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();
    
    private WorkbenchDataManager workbenchDataManager;
    
    private List<Long> projectAccessList = new LinkedList<Long>();

    protected synchronized void closeExcessSessionFactory() {
        if (projectAccessList.size() - 1 > getMaxCachedSessionFactories()) {
            return;
        }
        
        for (int index = projectAccessList.size() - 1; index >= getMaxCachedSessionFactories() - 1; index--) {
            Long projectId = projectAccessList.get(index);
            
            // close the session factory for the project
            SessionFactory sessionFactory = sessionFactoryCache.get(projectId);
            if (sessionFactory != null) {
                sessionFactory.close();
            }
            
            // remove the SessionFactory instance from our session factory cache
            sessionFactoryCache.remove(projectId);
            projectAccessList.remove(index);
        }
    }
    
  
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
            sessionProvider = new HibernateSessionPerRequestProvider(sessionFactory);
        }else{
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
