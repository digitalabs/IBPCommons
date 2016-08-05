
package org.generationcp.commons.spring.util;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This is the spring bean managed version of some of the methods used in the org.generationcp.commons.util.ContextUtil class 
 */
public class ContextUtil {

	static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

	@Resource
	private HttpServletRequest request;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	private static Cache<CropBasedContextInfo, Integer> localUserCache =
			CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(60, TimeUnit.MINUTES).build();

	public String getCurrentProgramUUID() {
		Project program = org.generationcp.commons.util.ContextUtil.getProjectInContext(this.workbenchDataManager, this.request);
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

	public Project getProjectInContext() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil.getProjectInContext(this.workbenchDataManager, this.request);
	}


	public int getCurrentUserLocalId() {
		final ContextInfo contextInfo = this.getContextInfoFromSession();
		try {
			Project projectInContext = getProjectInContext();
			Integer localUserId = localUserCache.get(new CropBasedContextInfo(contextInfo, projectInContext.getCropType().getCropName()),
					new Callable<Integer>() {

						@Override
						public Integer call() {
							return ContextUtil.this.workbenchDataManager.getLocalIbdbUserId(contextInfo.getLoggedInUserId(),
									contextInfo.getSelectedProjectId());

						}
					});
			if (localUserId != null) {
				return localUserId.intValue();
			}
			throw new IllegalStateException("Unable to retrive local id for logged in user id '%s' and project '%s'."
					+ " Please contact administrator for further information.");
		} catch (ExecutionException e) {
			throw new IllegalStateException("Unable to retrive local id for logged in user id '%s' and project '%s'."
					+ " Please contact administrator for further information.");
		}

	}

	public int getCurrentWorkbenchUserId() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request);
	}

	public User getCurrentWorkbenchUser() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUser(this.workbenchDataManager, this.request);
	}

	public String getCurrentWorkbenchUsername() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil.getCurrentWorkbenchUsername(this.workbenchDataManager, this.request);
	}

	public void logProgramActivity(String activityTitle, String activityDescription) throws MiddlewareQueryException {
		Project currentProject = this.getProjectInContext();
		User currentUser = this.getCurrentWorkbenchUser();

		ProjectActivity projAct = new ProjectActivity(currentProject.getProjectId().intValue(), currentProject, activityTitle,
				activityDescription, currentUser, new Date());

		this.workbenchDataManager.addProjectActivity(projAct);
	}
}
