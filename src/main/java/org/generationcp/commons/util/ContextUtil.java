
package org.generationcp.commons.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ContextUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);
	
	
	/**
	 * Main goal prevent excessive querying to retrieve project information.
	 */
	private static final Cache<Long, Project> PROJECTS_CACHE = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(60, TimeUnit.MINUTES).build();
	
	/**
	 * Main goal prevent excessive querying to retrieve user information information.
	 */
	private static final Cache<Integer, WorkbenchUser> USERS_CACHE = CacheBuilder.newBuilder().maximumSize(100).
			expireAfterWrite(60, TimeUnit.MINUTES).build();

	
	public static Project getProjectInContext(WorkbenchDataManager workbenchDataManager, HttpServletRequest request)
			throws MiddlewareQueryException {

		ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
		
		
		if(contextInfo != null) {
			Long selectedProjectId = contextInfo.getSelectedProjectId();
			
			if(selectedProjectId !=null && PROJECTS_CACHE.asMap().containsKey(selectedProjectId)) {
				return PROJECTS_CACHE.asMap().get(selectedProjectId);
			}
		}
		
		Project project = null;
		boolean resolvedFromSessionContext = false;

		if (contextInfo != null) {
			resolvedFromSessionContext = true;
			project = workbenchDataManager.getProjectById(contextInfo.getSelectedProjectId());
		} else {
			project = workbenchDataManager.getLastOpenedProjectAnyUser();
		}

		if (project != null) {
			ContextUtil.LOG.info("Selected project is: " + project.getProjectName() + ". Id: " + project.getProjectId() + ". Resolved "
					+ (resolvedFromSessionContext ? "from session context." : "using single user local install fallback method."));
			PROJECTS_CACHE.put(project.getProjectId(), project);
			return project;
		}

		throw new MiddlewareQueryException("Could not resolve selected project in Workbench.");
	}

	public static Integer getCurrentWorkbenchUserId(WorkbenchDataManager workbenchDataManager, HttpServletRequest request)
			throws MiddlewareQueryException {
		ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);

		Integer currentWorkbenchUserId = null;
		boolean resolvedFromSessionContext = false;
		Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);

		if (contextInfo != null) {
			resolvedFromSessionContext = true;
			currentWorkbenchUserId = contextInfo.getLoggedInUserId();
		} else if (userIdCookie != null) {
			currentWorkbenchUserId = Integer.parseInt(userIdCookie.getValue());
		}

		if (currentWorkbenchUserId != null) {
			ContextUtil.LOG.info("Logged in Workbench user id is: " + currentWorkbenchUserId + ". Resolved "
					+ (resolvedFromSessionContext ? "from session context." : "using single user local install fallback method."));
			return currentWorkbenchUserId;
		}

		throw new MiddlewareQueryException("Could not resolve current user id in Workbench.");
	}
	

	
	public static WorkbenchUser getCurrentWorkbenchUser(WorkbenchDataManager workbenchDataManager, HttpServletRequest request)
			throws MiddlewareQueryException {
		ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		WorkbenchUser user = null;
		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			// resolve from token if existing
			List<WorkbenchUser> matchedUsers = workbenchDataManager.getUserByName(userName, 0, 1, Operation.EQUAL);

			if (matchedUsers != null && !matchedUsers.isEmpty()) {
				user = matchedUsers.get(0);
			}

		} else if (contextInfo.getLoggedInUserId() != null) {
			user = getUserById(workbenchDataManager, contextInfo.getLoggedInUserId());
		} else {
			// resolve from cookie or session
			user = getUserById(workbenchDataManager, ContextUtil.getCurrentWorkbenchUserId(workbenchDataManager, request));
		}

		return user;
	}

	public static String getCurrentWorkbenchUsername(WorkbenchDataManager workbenchDataManager, HttpServletRequest request)
			throws MiddlewareQueryException {
		ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			return userName;

		} else if (contextInfo.getLoggedInUserId() != null) {
			userName = getUserById(workbenchDataManager, contextInfo.getLoggedInUserId()).getName();

		} else {
			// resolve from cookie or session
			userName = getUserById(workbenchDataManager, ContextUtil.getCurrentWorkbenchUserId(workbenchDataManager, request)).getName();
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
		if (contextInfo != null) {
			return ContextUtil.getContextParameterString(contextInfo.getLoggedInUserId(), contextInfo.getSelectedProjectId());
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

		if (loggedInUserId != null) {
			contextParameters.append(ContextUtil.addQueryParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID, loggedInUserId.toString()));
		}

		if (selectedProjectId != null) {
			contextParameters
					.append(ContextUtil.addQueryParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID, selectedProjectId.toString()));
		}

		return contextParameters.toString();
	}

	public static String addQueryParameter(String parameterName, String parameterValue) {
		return "&" + parameterName + "=" + parameterValue;
	}

	public static boolean isStaticResourceRequest(String requestUri) {
		if (requestUri.contains("/static/") || requestUri.endsWith(".js") || requestUri.endsWith(".css") || requestUri.endsWith(".png")
				|| requestUri.endsWith(".gif") || requestUri.endsWith(".jpg") || requestUri.endsWith(".woff")) {
			return true;
		}
		return false;
	}

	public static ContextInfo getContextInfoFromRequest(HttpServletRequest request) {
		Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		Integer userId = ContextUtil.getParamAsInt(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		String authToken = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);

		return new ContextInfo(userId, selectedProjectId, null != authToken ? authToken : "");

	}

	public static void setContextInfo(HttpServletRequest request, Integer userId, Long projectId, String authToken) {

			WebUtils.setSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO,
					new ContextInfo(userId, projectId,
							authToken));
	}
	
	static WorkbenchUser getUserById(final WorkbenchDataManager workbenchDataManager, final Integer userId) {
		final FunctionBasedGuavaCacheLoader<Integer, WorkbenchUser> cacheLoader =
				new FunctionBasedGuavaCacheLoader<Integer, WorkbenchUser>(USERS_CACHE, new Function<Integer, WorkbenchUser>() {

					@Override
					public WorkbenchUser apply(Integer key) {
						return workbenchDataManager.getUserById(key);
					}
				});
		
		final Optional<WorkbenchUser> loadedUserId = cacheLoader.get(userId);

		if (loadedUserId.isPresent()) {
			return loadedUserId.get();
		}
		
		return null;
	}

}
