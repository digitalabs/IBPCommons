package org.generationcp.commons.spring.util;

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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * This is the spring bean managed version of some of the methods used in
 * the org.generationcp.commons.util.ContextUtil class
 * User: Daniel Villafuerte
 * Date: 1/20/2015
 * Time: 5:14 PM
 */
public class ContextUtil {

	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

	@Resource
	private HttpServletRequest request;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	public String getCurrentProgramUUID() {
		try {
			return workbenchDataManager
					.getProjectById(getContextInfoFromSession().getSelectedProjectId())
					.getUniqueID();
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		return "";
	}

	public ContextInfo getContextInfoFromSession() {
		return (ContextInfo) WebUtils
				.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
	}

	public ContextInfo getContextInfoFromRequest() {
		return org.generationcp.commons.util.ContextUtil.getContextInfoFromRequest(request);
	}

	public Project getProjectInContext() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil
				.getProjectInContext(workbenchDataManager, request);
	}

	public int getCurrentUserLocalId() throws MiddlewareQueryException {
		ContextInfo contextInfo = this.getContextInfoFromSession();
		return workbenchDataManager.getLocalIbdbUserId(contextInfo.getloggedInUserId(),
				contextInfo.getSelectedProjectId());
	}

	public int getCurrentWorkbenchUserId() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil
				.getCurrentWorkbenchUserId(workbenchDataManager, request);
	}

	public User getCurrentWorkbenchUser() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil
				.getCurrentWorkbenchUser(workbenchDataManager, request);
	}

	public String getCurrentWorkbenchUsername() throws MiddlewareQueryException {
		return org.generationcp.commons.util.ContextUtil
				.getCurrentWorkbenchUsername(workbenchDataManager, request);
	}

	public void logProgramActivity(String activityTitle, String activityDescription)
			throws MiddlewareQueryException {
		Project currentProject = this.getProjectInContext();
		User currentUser = this.getCurrentWorkbenchUser();

		ProjectActivity projAct = new ProjectActivity(
				currentProject.getProjectId().intValue(),
				currentProject,
				activityTitle,
				activityDescription,
				currentUser,
				new Date());

		workbenchDataManager.addProjectActivity(projAct);
	}
}