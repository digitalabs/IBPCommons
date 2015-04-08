package org.generationcp.commons.util;

import javax.annotation.Resource;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by EfficioDaniel on 3/31/2015.
 */
public class CrossExpansionRule {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrossExpansionRule.class);
	
	@Resource
	private ContextUtil contextUtil;
	
    private int wheatMaxLevel;
    private int wheatNameType;
    private int nonWheatMaxLevel;
    private int nonWheatNameType;


    public CrossExpansionRule(){

    }
   
    public int getWheatMaxLevel() {
        return wheatMaxLevel;
    }

    public void setWheatMaxLevel(int wheatMaxLevel) {
        this.wheatMaxLevel = wheatMaxLevel;
    }

    public int getWheatNameType() {
        return wheatNameType;
    }

    public void setWheatNameType(int wheatNameType) {
        this.wheatNameType = wheatNameType;
    }

    public int getNonWheatMaxLevel() {
        return nonWheatMaxLevel;
    }

    public void setNonWheatMaxLevel(int nonWheatMaxLevel) {
        this.nonWheatMaxLevel = nonWheatMaxLevel;
    }

    public int getNonWheatNameType() {
        return nonWheatNameType;
    }

    public void setNonWheatNameType(int nonWheatNameType) {
        this.nonWheatNameType = nonWheatNameType;
    }

    public boolean isWheat() {
    	try {
			if("Wheat".equalsIgnoreCase(contextUtil.getProjectInContext().getCropType().getCropName())) {
               return true;
			}
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
    	return false;
    }

    
    public int getMaxLevelStoppageRule(){
        if(isWheat()){
            return getWheatMaxLevel();
        }else{
            return getNonWheatMaxLevel();
        }
    }

    public int getNameTypeStoppageRule(){
        if(isWheat()){
            return getWheatNameType();
        }else{
            return getNonWheatNameType();
        }
    }
}
