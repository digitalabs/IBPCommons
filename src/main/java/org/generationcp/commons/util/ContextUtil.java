
package org.generationcp.commons.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContextUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

	/**
	 * Main goal prevent excessive querying to retrieve project information.
	 */
	private static final Cache<Long, Project> PROJECTS_CACHE =
		CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * Main goal prevent excessive querying to retrieve user information information.
	 */
	private static final Cache<Integer, WorkbenchUser> USERS_CACHE = CacheBuilder.newBuilder().maximumSize(100).
		expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * Use {@link #getProject(WorkbenchDataManager, HttpServletRequest)} when an absent project is a valid scenario
	 */
	public static Project getProjectInContext(final WorkbenchDataManager workbenchDataManager, final HttpServletRequest request) {
		final Optional<Project> project = getProject(workbenchDataManager, request);
		if (!project.isPresent()) {
			throw new MiddlewareQueryException("Could not resolve selected project in Workbench.");
		}
		return project.get();
	}

	public static Optional<Project> getProject(final WorkbenchDataManager workbenchDataManager, final HttpServletRequest request) {

		final ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
		
		
		if(contextInfo != null) {
			final Long selectedProjectId = contextInfo.getSelectedProjectId();
			
			if(selectedProjectId !=null && PROJECTS_CACHE.asMap().containsKey(selectedProjectId)) {
				return Optional.of(PROJECTS_CACHE.asMap().get(selectedProjectId));
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
			return Optional.of(project);
		}

		return Optional.absent();
	}

	public static Integer getCurrentWorkbenchUserId(final HttpServletRequest request) {
		final ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);

		Integer currentWorkbenchUserId = null;
		boolean resolvedFromSessionContext = false;
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);

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



	public static WorkbenchUser getCurrentWorkbenchUser(final UserService userService, final HttpServletRequest request) {
		final ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		final String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		WorkbenchUser user = null;
		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			// resolve from token if existing // TODO cache like getUserById
			final List<WorkbenchUser> matchedUsers = userService.getUserByName(userName, 0, 1, Operation.EQUAL);

			if (matchedUsers != null && !matchedUsers.isEmpty()) {
				user = matchedUsers.get(0);
			}

		} else if (contextInfo.getLoggedInUserId() != null) {
			user = getUserById(userService, contextInfo.getLoggedInUserId());
		} else {
			// resolve from cookie or session
			user = getUserById(userService, ContextUtil.getCurrentWorkbenchUserId(request));
		}

		return user;
	}

	public static String getCurrentWorkbenchUsername(final UserService userService, final HttpServletRequest request) {
		final ContextInfo contextInfo = ContextUtil.getContextInfoFromRequest(request);
		String userName = SecurityUtil.decodeToken(contextInfo.getAuthToken());

		if (!StringUtil.isEmptyOrWhitespaceOnly(userName)) {
			return userName;

		} else if (contextInfo.getLoggedInUserId() != null) {
			userName = getUserById(userService, contextInfo.getLoggedInUserId()).getName();

		} else {
			// resolve from cookie or session
			userName = getUserById(userService, ContextUtil.getCurrentWorkbenchUserId(request)).getName();
		}

		return userName;
	}

	public static Long getParamAsLong(final HttpServletRequest request, final String paramName) {

		Long id = null;
		if (!StringUtils.isBlank(request.getParameter(paramName))) {
			try {
				id = new Long(request.getParameter(paramName));
			} catch (final NumberFormatException e) {
				id = null;
			}
		}
		return id;
	}

	public static Integer getParamAsInt(final HttpServletRequest request, final String paramName) {

		Integer id = null;
		if (!StringUtils.isBlank(request.getParameter(paramName))) {
			try {
				id = new Integer(request.getParameter(paramName));
			} catch (final NumberFormatException e) {
				id = null;
			}
		}
		return id;
	}

	public static String getContextParameterString(final ContextInfo contextInfo) {
		if (contextInfo != null) {
			return ContextUtil.getContextParameterString(contextInfo.getLoggedInUserId(), contextInfo.getSelectedProjectId());
		}
		return "";
	}

	public static String getContextParameterString(final Integer loggedInUserId, final Long selectedProjectId) {

		final StringBuffer contextParameters = new StringBuffer();

		if (loggedInUserId != null) {
			contextParameters.append(ContextUtil.addQueryParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID, loggedInUserId.toString()));
		}

		if (selectedProjectId != null) {
			contextParameters
					.append(ContextUtil.addQueryParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID, selectedProjectId.toString()));
		}

		return contextParameters.toString();
	}

	public static String addQueryParameter(final String parameterName, final String parameterValue) {
		return "&" + parameterName + "=" + parameterValue;
	}

	public static boolean isStaticResourceRequest(final String requestUri) {
		if (requestUri.contains("/static/") || requestUri.endsWith(".js") || requestUri.endsWith(".css") || requestUri.endsWith(".png")
				|| requestUri.endsWith(".gif") || requestUri.endsWith(".jpg") || requestUri.endsWith(".woff")) {
			return true;
		}
		return false;
	}

	public static ContextInfo getContextInfoFromRequest(final HttpServletRequest request) {
		final Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		final Integer userId = ContextUtil.getParamAsInt(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final String authToken = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);

		return new ContextInfo(userId, selectedProjectId, null != authToken ? authToken : "");

	}

	public static void setContextInfo(final HttpServletRequest request, final Integer userId, final Long projectId, final String authToken) {

			WebUtils.setSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO,
					new ContextInfo(userId, projectId,
							authToken));
	}

	static WorkbenchUser getUserById(final UserService userService, final Integer userId) {
		final FunctionBasedGuavaCacheLoader<Integer, WorkbenchUser> cacheLoader =
				new FunctionBasedGuavaCacheLoader<>(USERS_CACHE, new Function<Integer, WorkbenchUser>() {

					@Override
					public WorkbenchUser apply(final Integer key) {
						return userService.getUserById(key);
					}
				});

		final Optional<WorkbenchUser> loadedUserId = cacheLoader.get(userId);

		if (loadedUserId.isPresent()) {
			return loadedUserId.get();
		}

		return null;
	}

}
