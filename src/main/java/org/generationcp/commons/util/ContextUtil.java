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
		boolean resolvedFromSessionContext = false;
		
    	if(contextInfo != null) {
    		resolvedFromSessionContext = true;
    		project = workbenchDataManager.getProjectById(contextInfo.getSelectedProjectId());
    	} else {
    		project = workbenchDataManager.getLastOpenedProjectAnyUser();    		
    	}
    	
    	if(project != null) {
			LOG.info("Selected project is: " + project.getProjectName() + ". Id: " + project.getProjectId()
					+ ". Local DB: " + project.getLocalDbName() + ". Central DB: " + project.getCentralDbName()
					+ ". Resolved " + (resolvedFromSessionContext ? "from session context." : "using single user local install fallback method."));
			return project;
    	}
    	
    	throw new MiddlewareQueryException("Could not resolve selected project in Workbench.");
	}
	
	public static Integer getCurrentWorkbenchUserId(WorkbenchDataManager workbenchDataManager, HttpServletRequest request) throws MiddlewareQueryException {
		
		ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
		Integer currentWorkbenchUserId = null;
		boolean resolvedFromSessionContext = false;
		
		if(contextInfo != null) {
			resolvedFromSessionContext = true;
			currentWorkbenchUserId = contextInfo.getloggedInUserId();
		} else {
			currentWorkbenchUserId = workbenchDataManager.getWorkbenchRuntimeData().getUserId();
		}		
		
		if(currentWorkbenchUserId != null) {
			LOG.info("Logged in Workbench user id is: " + currentWorkbenchUserId 
				 + ". Resolved " + (resolvedFromSessionContext ? "from session context." : "using single user local install fallback method."));		
			return currentWorkbenchUserId;
		}
		
		throw new MiddlewareQueryException("Could not resolve current user id in Workbench.");
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
	
	public static Integer getParamAsInt(HttpServletRequest request, String paramName) {
		
		Integer id = null;
		if (!StringUtils.isBlank(request.getParameter(paramName))) {
			try {
				id = new Integer(request.getParameter(paramName));
			} catch (NumberFormatException e) {
				id = null;
			}
		}
		return id;
	}
	
	public static String getContextParameterString(ContextInfo contextInfo) {
		if(contextInfo != null) {
			return ContextUtil.getContextParameterString(contextInfo.getloggedInUserId(), contextInfo.getSelectedProjectId());
		}
		return "";
	}
	
	public static String getContextParameterString(HttpServletRequest request) {
		Integer loggedInUserId = ContextUtil.getParamAsInt(request, ContextConstants.PARAM_LOGGED_IN_USER_ID); 
		Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		return ContextUtil.getContextParameterString(loggedInUserId, selectedProjectId);
	}
	
	public static String getContextParameterString(Integer loggedInUserId, Long selectedProjectId) {

		StringBuffer contextParameters = new StringBuffer();
		
		if(loggedInUserId != null) {
			contextParameters.append(addQueryParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID, loggedInUserId.toString()));
		}
		
		if(selectedProjectId != null) {
			contextParameters.append(addQueryParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID, selectedProjectId.toString()));
		}
		
		return contextParameters.toString();
	}

	private static String addQueryParameter(String parameterName, String parameterValue) {
		return "&" + parameterName + "=" + parameterValue;
	}
	
	
	public static boolean isStaticResourceRequest(String requestUri) {
		if (requestUri.contains("/static/") 
				|| requestUri.endsWith(".js") || requestUri.endsWith(".css") 
				|| requestUri.endsWith(".png") || requestUri.endsWith(".gif") || requestUri.endsWith(".jpg")
				|| requestUri.endsWith(".woff")) {
			return true;
		}
		return false;
	}

}
