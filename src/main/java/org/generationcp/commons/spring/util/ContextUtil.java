
package org.generationcp.commons.spring.util;

import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.springframework.web.util.WebUtils;

/**
 * This is the spring bean managed version of some of the methods used in the org.generationcp.commons.util.ContextUtil class User: Daniel
 * Villafuerte Date: 1/20/2015 Time: 5:14 PM
 */
public class ContextUtil {

	@Resource
	private HttpServletRequest request;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

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

	public int getCurrentUserLocalId() throws MiddlewareQueryException {
		ContextInfo contextInfo = this.getContextInfoFromSession();
		return this.workbenchDataManager.getLocalIbdbUserId(contextInfo.getloggedInUserId(), contextInfo.getSelectedProjectId());
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

		ProjectActivity projAct =
				new ProjectActivity(currentProject.getProjectId().intValue(), currentProject, activityTitle, activityDescription,
						currentUser, new Date());

		this.workbenchDataManager.addProjectActivity(projAct);
	}
}
