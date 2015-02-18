package org.generationcp.commons.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import java.util.List;

public class ContextUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

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
					+ ". Resolved " + (resolvedFromSessionContext ? "from session context." : "using single user local install fallback method."));
			return project;
    	}
    	
    	throw new MiddlewareQueryException("Could not resolve selected project in Workbench.");
	}
	
	public static Integer getCurrentWorkbenchUserId(WorkbenchDataManager workbenchDataManager, HttpServletRequest request) throws MiddlewareQueryException {
		ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);

		Integer currentWorkbenchUserId = null;
		boolean resolvedFromSessionContext = false;
		Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);


		if(contextInfo != null) {
			resolvedFromSessionContext = true;
			currentWorkbenchUserId = contextInfo.getloggedInUserId();
		} else if (userIdCookie != null) {
			currentWorkbenchUserId = Integer.parseInt(userIdCookie.getValue());
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

	public static User getCurrentWorkbenchUser(WorkbenchDataManager workbenchDataManager, HttpServletRequest request) throws MiddlewareQueryException {
		ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		User user = null;
		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			// resolve from token if existing
			List<User> matchedUsers = workbenchDataManager.getUserByName(userName,0,1,
					Operation.EQUAL);

			if (matchedUsers != null && !matchedUsers.isEmpty()) {
				user = matchedUsers.get(0);
			}
		} else if (contextInfo.getloggedInUserId() != null) {
			user = workbenchDataManager.getUserById(contextInfo.getloggedInUserId());
		} else {
			// resolve from cookie or session
			user = workbenchDataManager.getUserById(ContextUtil.getCurrentWorkbenchUserId(workbenchDataManager,request));
		}

		return user;
	}

	public static String getCurrentWorkbenchUsername(WorkbenchDataManager workbenchDataManager, HttpServletRequest request) throws MiddlewareQueryException {
		ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			return userName;
		} else if (contextInfo.getloggedInUserId() != null) {
			userName = workbenchDataManager.getUserById(contextInfo.getloggedInUserId()).getName();
		} else {
			// resolve from cookie or session
			userName = workbenchDataManager.getUserById(ContextUtil.getCurrentWorkbenchUserId(workbenchDataManager,request)).getName();
		}

		return userName;
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
		Long selectedProjectId = ContextUtil.getParamAsLong(request,
				ContextConstants.PARAM_SELECTED_PROJECT_ID);
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

	public static String addQueryParameter(String parameterName, String parameterValue) {
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

	public static ContextInfo  getContextInfoFromRequest(HttpServletRequest request) {
		Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		Integer userId = ContextUtil.getParamAsInt(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		String authToken = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);

		return new ContextInfo(userId,selectedProjectId,(null != authToken) ? authToken : "");

	}

}
