package org.generationcp.commons.spring.customscope;

import org.generationcp.middleware.manager.ManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.SessionScope;

public class ManagerFactorySessionScope extends SessionScope {
	
	private final static Logger LOG = LoggerFactory.getLogger(ManagerFactorySessionScope.class);
	
	ManagerFactory managerFactory;

	@Override
	public Object get(String name, ObjectFactory objectFactory) {
		
		Object obj = super.get(name, objectFactory);
		if (managerFactory == null){
			managerFactory = (ManagerFactory) obj;
		}
		
		LOG.info("get " + obj.toString());
		
		return obj;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		// TODO Auto-generated method stub
		
		if (this.managerFactory!= null) { 
			this.managerFactory.close();		
		}
		this.managerFactory = null;
		
		LOG.info("registerDestructionCallback");
	
		super.registerDestructionCallback(name, callback);
		
	}

	

	public ManagerFactorySessionScope() {
		
	}


}
