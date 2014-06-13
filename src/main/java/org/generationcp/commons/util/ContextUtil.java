package org.generationcp.commons.util;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

public class ContextUtil {

	private final static Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

	public static Project getProjectInContext(WorkbenchDataManager workbenchDataManager, HttpServletRequest request) throws MiddlewareQueryException {
		
		ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);    	
    	
    	if(contextInfo != null) {
    		Project project = workbenchDataManager.getProjectById(contextInfo.getSelectedProjectId());
    		LOG.debug("Project in context is: " + project.getProjectName() + " [id = " + project.getProjectId() + "]. "
    				+ "Local DB: " + project.getLocalDbName() + ". Central DB: " + project.getCentralDbName());
    		return project;
    	}
    	
    	// Should never come here in theory. If and when we do, it means the context parameters are most likely being lost due to
    	// session.invalidate() call being made somwhere in the request handling cycle where it should not be.
    	
    	throw new MiddlewareQueryException("No information about the current project (program) found in context. "
    			+ "Unable to determine program local/central databases to connect to.");
	}
}
