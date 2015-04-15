package org.generationcp.commons.util;

import javax.annotation.Resource;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.util.CrossExpansionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by EfficioDaniel on 3/31/2015.
 */
public class CrossExpansionProperties {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrossExpansionProperties.class);
	
	@Resource
	private ContextUtil contextUtil;
	private boolean isCimmytUser;
    private int wheatMaxLevel;
    private int nonWheatMaxLevel;
    

    public CrossExpansionProperties(){

    }
    
    public CrossExpansionRule getCrossExpansionRule(){
    	return new CrossExpansionRule(isCimmyWheat(), getMaxLevelStoppageRule());
    }
   
    public int getWheatMaxLevel() {
        return wheatMaxLevel;
    }

    public void setWheatMaxLevel(int wheatMaxLevel) {
        this.wheatMaxLevel = wheatMaxLevel;
    }   

    public int getNonWheatMaxLevel() {
        return nonWheatMaxLevel;
    }

    public void setNonWheatMaxLevel(int nonWheatMaxLevel) {
        this.nonWheatMaxLevel = nonWheatMaxLevel;
    }

    public boolean isCimmyWheat() {
    	try {
			if(isCimmytUser() && "Wheat".equalsIgnoreCase(contextUtil.getProjectInContext().getCropType().getCropName())) {
               return true;
			}
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
    	return false;
    }

    
    public int getMaxLevelStoppageRule(){
        if(isCimmyWheat()){
            return getWheatMaxLevel();
        }else{
            return getNonWheatMaxLevel();
        }
    }

	public boolean isCimmytUser() {
		return isCimmytUser;
	}

	public void setCimmytUser(boolean isCimmytUser) {
		this.isCimmytUser = isCimmytUser;
	}
    
    
}
