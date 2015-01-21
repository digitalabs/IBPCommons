package org.generationcp.commons.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;


public abstract class ManagerFactoryBase {

    protected String  dbHost = "localhost";
    protected Integer dbPort = 13306;
    protected String  dbUsername = "root";
    protected String  dbPassword = "";
    
    protected Map<Long, SessionFactory> sessionFactoryCache = new HashMap<Long, SessionFactory>();
    protected int maxCachedSessionFactories = 10;
    
    public ManagerFactoryBase() {
    	
    }
	
	public String getDbHost() {
		return dbHost;
	}
	
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}
	
	public Integer getDbPort() {
		return dbPort;
	}
	
	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}
	
	public String getDbUsername() {
		return dbUsername;
	}
	
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}
	
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public int getMaxCachedSessionFactories() {
		return maxCachedSessionFactories;
	}

	public void setMaxCachedSessionFactories(int maxCachedSessionFactories) {
		this.maxCachedSessionFactories = maxCachedSessionFactories;
	}
}
