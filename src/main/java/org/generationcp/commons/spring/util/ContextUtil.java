package org.generationcp.commons.spring.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This is the spring bean managed version of some of the methods used in the org.generationcp.commons.util.ContextUtil class
 */
public class ContextUtil {

	private static final String NO_LOCAL_USER_ID_FOUND_MESSAGE = "Unable to retrive local id for logged in user id '%s' and project '%s'."
			+ " Please contact administrator for further information.";

	@Resource
	private HttpServletRequest request;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	/**
	 * Main goal is to prevent excessive queries to get local user names. This is a global cache that will expire every 10 minutes.
	 */
	private static final Cache<CropBasedContextInfo, Integer> localUserCache =
			CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(10, TimeUnit.MINUTES).build();

	public String getCurrentProgramUUID() {
		final Project program = org.generationcp.commons.util.ContextUtil.getProjectInContext(this.workbenchDataManager, this.request);
		if (program != null) {
			return program.getUniqueID();
		}
		return null;
	}

	public ContextInfo getContextInfoFromSession() {
		return (ContextInfo) WebUtils.getSessionAttribute(this.request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
	}

	public ContextInfo getContextInfoFromRequest() {
		return org.generationcp.commons.util.ContextUtil.getContextInfoFromRequest(this.request);
	}

	public Project getProjectInContext() {
		return org.generationcp.commons.util.ContextUtil.getProjectInContext(this.workbenchDataManager, this.request);
	}

	public int getCurrentUserLocalId() {
		final ContextInfo contextInfo = this.getContextInfoFromSession();
		try {
			final Project projectInContext = getProjectInContext();
			final Integer localUserId = localUserCache
					.get(new CropBasedContextInfo(contextInfo, projectInContext.getCropType().getCropName()), new Callable<Integer>() {

						@Override
						public Integer call() {
							return ContextUtil.this.workbenchDataManager
									.getLocalIbdbUserId(contextInfo.getLoggedInUserId(), contextInfo.getSelectedProjectId());
						}
					});
			if (localUserId != null) {
				return localUserId.intValue();
			}
			throw new IllegalStateException(NO_LOCAL_USER_ID_FOUND_MESSAGE);
		} catch (final ExecutionException e) {
			throw new IllegalStateException(NO_LOCAL_USER_ID_FOUND_MESSAGE, e);
		}

	}

	public int getCurrentWorkbenchUserId() {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request);
	}

	public WorkbenchUser getCurrentWorkbenchUser() {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUser(this.workbenchDataManager, this.request);
	}

	public String getCurrentWorkbenchUsername() {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUsername(this.workbenchDataManager, this.request);
	}

	public void logProgramActivity(final String activityTitle, final String activityDescription) {
		final Project currentProject = this.getProjectInContext();
		final WorkbenchUser currentUser = this.getCurrentWorkbenchUser();

		final ProjectActivity projAct =
				new ProjectActivity(currentProject.getProjectId().intValue(), currentProject, activityTitle, activityDescription,
						currentUser, new Date());

		this.workbenchDataManager.addProjectActivity(projAct);
	}

	public Integer getCurrentIbdbUserId() {
		return this.workbenchDataManager
			.getCurrentIbdbUserId(Long.valueOf(this.getProjectInContext().getProjectId().toString()), this.getCurrentWorkbenchUserId());

	}

	public Integer getIbdbUserId(final Integer workbenchUserId) {
		return this.workbenchDataManager
			.getCurrentIbdbUserId(Long.valueOf(this.getProjectInContext().getProjectId().toString()), workbenchUserId);

	}

}
