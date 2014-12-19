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

/**
 * The {@link DefaultManagerFactoryProvider} is an implementation of
 * {@link ManagerFactoryProvider} that expects central databases are named using
 * the format: <br>
 * <code>ibdb_&lt;crop type&gt;_central</code><br>
 * and local databases are named using the format: <br>
 * <code>&lt;crop type&gt;_&lt;project id&gt;_local</code><br>
 * 
 * @author Glenn Marintes
 */
public class DefaultManagerFactoryProvider implements ManagerFactoryProvider, HttpRequestAware {
    
	private Map<Long, SessionFactory> localSessionFactories = new HashMap<Long, SessionFactory>();
    
    private final static ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<HttpServletRequest>();
    
    private Map<HttpServletRequest, HibernateSessionProvider> localSessionProviders = new HashMap<HttpServletRequest, HibernateSessionProvider>();
    
    private String localHost = "localhost";

    private Integer localPort = 13306;

    private String localUsername = "local";

    private String localPassword = "local";

    private int maxCachedLocalSessionFactories = 10;
    private List<Long> projectAccessList = new LinkedList<Long>();
    
    public DefaultManagerFactoryProvider() {
	}
    
    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public void setLocalUsername(String localUsername) {
        this.localUsername = localUsername;
    }

    public void setLocalPassword(String localPassword) {
        this.localPassword = localPassword;
    }

    protected synchronized void closeExcessLocalSessionFactory() {
        if (projectAccessList.size() - 1 > maxCachedLocalSessionFactories) {
            return;
        }
        
        for (int index = projectAccessList.size() - 1; 
                index >= maxCachedLocalSessionFactories - 1;
                index--) {
            Long projectId = projectAccessList.get(index);
            
            // close the session factory for the project
            SessionFactory sessionFactory = localSessionFactories.get(projectId);
            if (sessionFactory != null) {
                sessionFactory.close();
            }
            
            // remove the SessionFactory instance from our local session factory cache
            localSessionFactories.remove(projectId);
            projectAccessList.remove(index);
        }
    }

    @Override
    public synchronized ManagerFactory getManagerFactoryForProject(Project project) {
        SessionFactory localSessionFactory = localSessionFactories.get(project.getProjectId());
        String databaseName = null;
        if (localSessionFactory != null) {
            projectAccessList.remove(project.getProjectId());
        }
        
        databaseName = project.getDatabaseName();
    	
        if (localSessionFactory == null || localSessionFactory.isClosed()) {
            // close any excess cached session factory
            closeExcessLocalSessionFactory();
            
            DatabaseConnectionParameters params = new DatabaseConnectionParameters(
                    localHost, String.valueOf(localPort), databaseName, localUsername, localPassword);
            try {
                localSessionFactory = SessionFactoryUtil.openSessionFactory(params);
                localSessionFactories.put(project.getProjectId(), localSessionFactory);
            } catch (FileNotFoundException e) {
                throw new ConfigException("Cannot create a SessionFactory for " + project, e);
            }
        }
        
        // add this local session factory to the head of the access list
        projectAccessList.add(0, project.getProjectId());
        
        // get or create the HibernateSessionProvider for the current request
        HttpServletRequest request = CURRENT_REQUEST.get();
        HibernateSessionProvider localSessionProvider = localSessionProviders.get(request);
        if (localSessionProvider == null && localSessionFactory != null) {
            localSessionProvider = new HibernateSessionPerRequestProvider(localSessionFactory);
            localSessionProviders.put(request, localSessionProvider);
        }
        
        ManagerFactory factory = new ManagerFactory();
        factory.setSessionProvider(localSessionProvider);
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
        HibernateSessionProvider localSessionProvider = localSessionProviders.get(request);
        if (localSessionProvider != null) {
            localSessionProvider.close();
            localSessionProviders.remove(request);
        }
        
        CURRENT_REQUEST.remove();
    }

    @Override
    public synchronized void close() {
        for (HttpServletRequest request : localSessionProviders.keySet()) {
            HibernateSessionProvider provider = localSessionProviders.get(request);
            if (provider != null) {
                provider.close();
            }
        }
        
        for (Long projectId : localSessionFactories.keySet()) {
            SessionFactory factory = localSessionFactories.get(projectId);
            if (factory != null) {
                factory.close();
            }
        }
        localSessionFactories.clear();
        localSessionProviders.clear();
    }

    public void removeProjectFromLocalSession(long projectId) {
    	if(localSessionFactories!=null && localSessionFactories.containsKey(projectId)) {
    		localSessionFactories.get(projectId).close();
    		localSessionFactories.remove(projectId);
    	}
    }
}
