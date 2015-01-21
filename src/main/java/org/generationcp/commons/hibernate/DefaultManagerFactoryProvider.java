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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    
    private Map<HttpServletRequest, HibernateSessionProvider> sessionProviders = new HashMap<HttpServletRequest, HibernateSessionProvider>();
    
    private List<Long> projectAccessList = new LinkedList<Long>();
    
    public DefaultManagerFactoryProvider() {
	}
    
    protected synchronized void closeExcessSessionFactory() {
        if (projectAccessList.size() - 1 > maxCachedSessionFactories) {
            return;
        }
        
        for (int index = projectAccessList.size() - 1; index >= maxCachedSessionFactories - 1; index--) {
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

    @Override
    public synchronized ManagerFactory getManagerFactoryForProject(Project project) {
        SessionFactory sessionFactory = sessionFactoryCache.get(project.getProjectId());
        String databaseName = null;
        if (sessionFactory != null) {
            projectAccessList.remove(project.getProjectId());
        }
        
        databaseName = project.getDatabaseName();
    	
        if (sessionFactory == null || sessionFactory.isClosed()) {
            // close any excess cached session factory
            closeExcessSessionFactory();
            
            DatabaseConnectionParameters params = new DatabaseConnectionParameters(
                    dbHost, String.valueOf(dbPort), databaseName, dbUsername, dbPassword);
            try {
                sessionFactory = SessionFactoryUtil.openSessionFactory(params);
                sessionFactoryCache.put(project.getProjectId(), sessionFactory);
            } catch (FileNotFoundException e) {
                throw new ConfigException("Cannot create a SessionFactory for " + project, e);
            }
        }
        
        // add this session factory to the head of the access list
        projectAccessList.add(0, project.getProjectId());
        
        // get or create the HibernateSessionProvider for the current request
        HttpServletRequest request = CURRENT_REQUEST.get();
        HibernateSessionProvider sessionProvider = sessionProviders.get(request);
        if (sessionProvider == null && sessionFactory != null) {
            sessionProvider = new HibernateSessionPerRequestProvider(sessionFactory);
            sessionProviders.put(request, sessionProvider);
        }
        
        ManagerFactory factory = new ManagerFactory();
        factory.setSessionProvider(sessionProvider);
        factory.setDatabaseName(databaseName);
        return factory;
    }
    
    @Override
    public void onRequestStarted(HttpServletRequest request, HttpServletResponse response) {
        // remember the HttpServletRequest for this thread
        CURRENT_REQUEST.set(request);
    }
    
    @Override
    public void onRequestEnded(HttpServletRequest request, HttpServletResponse response) {
        HibernateSessionProvider sessionProvider = sessionProviders.get(request);
        if (sessionProvider != null) {
            sessionProvider.close();
            sessionProviders.remove(request);
        }
        CURRENT_REQUEST.remove();
    }

    @Override
    public synchronized void close() {
        for (HttpServletRequest request : sessionProviders.keySet()) {
            HibernateSessionProvider provider = sessionProviders.get(request);
            if (provider != null) {
                provider.close();
            }
        }
        
        for (Long projectId : sessionFactoryCache.keySet()) {
            SessionFactory factory = sessionFactoryCache.get(projectId);
            if (factory != null) {
                factory.close();
            }
        }
        sessionFactoryCache.clear();
        sessionProviders.clear();
    }

    public void removeProjectFromSessionCache(long projectId) {
    	if(sessionFactoryCache!=null && sessionFactoryCache.containsKey(projectId)) {
    		sessionFactoryCache.get(projectId).close();
    		sessionFactoryCache.remove(projectId);
    	}
    }
}
