
package org.generationcp.commons.hibernate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.hibernate.SessionFactory;

public abstract class ManagerFactoryBase {

	protected String dbHost = "localhost";
	protected Integer dbPort = 13306;
	protected String dbUsername = "root";
	protected String dbPassword = "";
	protected String pedigreeProfile = PedigreeFactory.PROFILE_DEFAULT;

	protected Map<Long, SessionFactory> sessionFactoryCache = new HashMap<Long, SessionFactory>();
	protected int maxCachedSessionFactories = 10;

	protected List<Long> projectAccessList = new LinkedList<Long>();

	public ManagerFactoryBase() {
	}

	protected synchronized void closeExcessSessionFactory() {
		if (this.projectAccessList.size() - 1 > this.getMaxCachedSessionFactories()) {
			return;
		}

		for (int index = this.projectAccessList.size() - 1; index >= this.getMaxCachedSessionFactories() - 1; index--) {
			Long projectId = this.projectAccessList.get(index);

			// close the session factory for the project
			SessionFactory sessionFactory = this.sessionFactoryCache.get(projectId);
			if (sessionFactory != null) {
				sessionFactory.close();
			}

			// remove the SessionFactory instance from our session factory cache
			this.sessionFactoryCache.remove(projectId);
			this.projectAccessList.remove(index);
		}
	}

	public String getDbHost() {
		return this.dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public Integer getDbPort() {
		return this.dbPort;
	}

	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbUsername() {
		return this.dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return this.dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public int getMaxCachedSessionFactories() {
		return this.maxCachedSessionFactories;
	}

	public void setMaxCachedSessionFactories(int maxCachedSessionFactories) {
		this.maxCachedSessionFactories = maxCachedSessionFactories;
	}

	public String getPedigreeProfile() {
		return this.pedigreeProfile;
	}

	public void setPedigreeProfile(String pedigreeProfile) {
		this.pedigreeProfile = pedigreeProfile;
	}
}
