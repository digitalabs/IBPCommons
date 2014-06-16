package org.generationcp.commons.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
    	
		Project project = null;
		
    	if(contextInfo != null) {
    		LOG.info("Found context information from session context attributes.");
    		project = workbenchDataManager.getProjectById(contextInfo.getSelectedProjectId());
    	} else {
    		// Fall-back to the old method so that single user, local installation scenario continues to work while we make all parts of BMS multi-user aware.
    		LOG.info("No context information found from session. Falling back to the single user mode method of determining Project in context.");
    		project = workbenchDataManager.getLastOpenedProjectAnyUser();    		
    	}
    	
		LOG.info("Project in context is: " + project.getProjectName() + " [id = " + project.getProjectId() + "]. "
				+ "Local DB: " + project.getLocalDbName() + ". Central DB: " + project.getCentralDbName());
		return project;
	}
	
	
	public static Long getParamAsLong(HttpServletRequest request, String paramName) {
		
		Long id = null;
		if (!StringUtils.isBlank(request.getParameter(paramName))) {
			try {
				id = new Long(request.getParameter(paramName));
			} catch (NumberFormatException e) {
				id = null;
			}
		}
		return id;
	}
	
	public static String getContextParameterString(ContextInfo contextInfo) {
		return ContextUtil.getContextParameterString(contextInfo.getloggedInUserId(), contextInfo.getSelectedProjectId());
	}
	
	public static String getContextParameterString(HttpServletRequest request) {
		Long loggedInUserId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_LOGGED_IN_USER_ID); 
		Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		return ContextUtil.getContextParameterString(loggedInUserId, selectedProjectId);
	}
	
	public static String getContextParameterString(Long loggedInUserId, Long selectedProjectId) {

		StringBuffer contextParameters = new StringBuffer();
		contextParameters
			.append(addQueryParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID, loggedInUserId))
			.append(addQueryParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID, selectedProjectId));

		return contextParameters.toString();
	}

	private static String addQueryParameter(String parameterName, Object parameterValue) {
		return "&" + parameterName + "=" + parameterValue;
	}

}
