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
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerRequestProvider;
import org.generationcp.middleware.hibernate.SessionFactoryUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.SessionFactory;

/**
 * The {@link DynamicManagerFactoryProvider} is an implementation of
 * {@link ManagerFactoryProvider} that expects central databases are named using
 * the format: <br>
 * <code>ibdb_&lt;crop type&gt;_central</code><br>
 * and local databases are named using the format: <br>
 * <code>&lt;crop type&gt;_&lt;project id&gt;_local</code><br>
 * 
 * @author Glenn Marintes
 */
public class DynamicManagerFactoryProvider implements ManagerFactoryProvider, HttpRequestAware {
	
	public DynamicManagerFactoryProvider() {
		
	}
	
	public DynamicManagerFactoryProvider(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
	
	private HibernateSessionPerRequestProvider localSessionProvider; 
	private HibernateSessionPerRequestProvider centralSessionProvider; 
	
    private Map<Long, SessionFactory> localSessionFactories = 
            new HashMap<Long, SessionFactory>();
    private Map<CropType, SessionFactory> centralSessionFactories = 
            new HashMap<CropType, SessionFactory>();
    
    
    private WorkbenchDataManager workbenchDataManager;
    
    private String localHost = "localhost";

    private Integer localPort = 13306;

    private String localUsername = "local";

    private String localPassword = "local";

    private String centralHost = "localhost";

    private Integer centralPort = 13306;

    private String centralUsername = "central";

    private String centralPassword = "central";
    
    private int maxCachedLocalSessionFactories = 10;
    
    private List<Long> projectAccessList = new LinkedList<Long>();

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

    public void setCentralHost(String centralHost) {
        this.centralHost = centralHost;
    }

    public void setCentralPort(Integer centralPort) {
        this.centralPort = centralPort;
    }

    public void setCentralUsername(String centralUsername) {
        this.centralUsername = centralUsername;
    }

    public void setCentralPassword(String centralPassword) {
        this.centralPassword = centralPassword;
    }
    
    protected synchronized void closeExcessLocalSessionFactory() {
        if (projectAccessList.size() - 1 > getMaxCachedLocalSessionFactories()) {
            return;
        }
        
        for (int index = projectAccessList.size() - 1; 
                index >= getMaxCachedLocalSessionFactories() - 1;
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
    
  
    public synchronized ManagerFactory createInstance() throws MiddlewareQueryException {
    	
    	String localDbName = null;
    	String centralDbName = null;
    	Project project = workbenchDataManager.getLastOpenedProjectAnyUser();
    	
        SessionFactory localSessionFactory = localSessionFactories.get(project.getProjectId());
        
        if (localSessionFactory != null) {
            projectAccessList.remove(project.getProjectId());
        }
        
        if (localSessionFactory == null || localSessionFactory.isClosed()) {
        	localDbName = project.getCropType().getLocalDatabaseNameWithProject(project);
            
            // close any excess cached session factory
            closeExcessLocalSessionFactory();
            
            DatabaseConnectionParameters params = new DatabaseConnectionParameters(
                    localHost, String.valueOf(localPort), localDbName, localUsername, localPassword);
            try {
                localSessionFactory = SessionFactoryUtil.openSessionFactory(params);
                localSessionFactories.put(project.getProjectId(), localSessionFactory);
            }
            catch (FileNotFoundException e) {
                throw new ConfigException("Cannot create a SessionFactory for " + project, e);
            }
        } else {
        	localDbName = project.getCropType().getLocalDatabaseNameWithProject(project);
        }
        
        // add this local session factory to the head of the access list
        projectAccessList.add(0, project.getProjectId());
        
        // get or create a central session factory
        SessionFactory centralSessionFactory = centralSessionFactories.get(project.getCropType());
        if ((centralSessionFactory == null || centralSessionFactory.isClosed()) 
                && project.getCropType().getCentralDbName() != null) {
            centralDbName = project.getCropType().getCentralDbName();
            
            DatabaseConnectionParameters params = 
                    new DatabaseConnectionParameters(centralHost, String.valueOf(centralPort), 
                            centralDbName, centralUsername, centralPassword);
            
            try {
                centralSessionFactory = SessionFactoryUtil.openSessionFactory(params);
                centralSessionFactories.put(project.getCropType(), centralSessionFactory);
            }
            catch (FileNotFoundException e) {
                throw new ConfigException("Cannot create a SessionFactory for " + project, e);
            }
        } else {
        	centralDbName = project.getCropType().getCentralDbName();
        }
        

        if (localSessionProvider == null && localSessionFactory != null) {
            localSessionProvider = new HibernateSessionPerRequestProvider(localSessionFactory);
        }else{
        	localSessionProvider.setSessionFactory(localSessionFactory);
        }
        
        if (centralSessionProvider == null && centralSessionFactory != null) {
            centralSessionProvider = new HibernateSessionPerRequestProvider(centralSessionFactory);
        }else{
        	centralSessionProvider.setSessionFactory(centralSessionFactory);
        }
        
        // create a ManagerFactory and set the HibernateSessionProviders
        // we don't need to set the SessionFactories here
        // since we want to a Session Per Request 
        ManagerFactory factory = new ManagerFactory();
        factory.setSessionProviderForLocal(localSessionProvider);
        factory.setSessionProviderForCentral(centralSessionProvider);
        factory.setCentralDatabaseName(centralDbName);
        factory.setLocalDatabaseName(localDbName);
        
        return factory;
    }


	@Override
	public void onRequestStarted(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestEnded(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ManagerFactory getManagerFactoryForProject(Project project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ManagerFactory getManagerFactoryForCropType(CropType cropType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		
		 if (localSessionProvider != null) {
	            localSessionProvider.close();
	        }
	        
	        if (centralSessionProvider != null) {
	            centralSessionProvider.close();
	        }
	        
	        //if (workbenchDataManager != null) {
	        //	workbenchDataManager.close();
	        //}
	        
	        
		
	}
	
	protected synchronized void closeAllSessionFactories() {
		
		for (Entry<Long, SessionFactory> entry : localSessionFactories.entrySet()){
			entry.getValue().close();
			localSessionFactories.remove(entry);
		}
   
		for (Entry<CropType, SessionFactory> entry : centralSessionFactories.entrySet()){
			entry.getValue().close();
			centralSessionFactories.remove(entry);
		}
       
    }

	public int getMaxCachedLocalSessionFactories() {
		return maxCachedLocalSessionFactories;
	}

	public void setMaxCachedLocalSessionFactories(
			int maxCachedLocalSessionFactories) {
		this.maxCachedLocalSessionFactories = maxCachedLocalSessionFactories;
	}

   
}
