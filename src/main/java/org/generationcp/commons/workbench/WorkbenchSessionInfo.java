package org.generationcp.commons.workbench;

/**
 * POJO used to expose information out of Workbench.
 * 
 * See org.generationcp.ibpworkbench.api.WorkbenchContextInfoResource and org.generationcp.ibpworkbench.api.WorkbenchContext in Workbench.
 *  
 * @author Naymesh Mistry
 *
 */
public class WorkbenchSessionInfo {
	
	private Integer userId; 
	private String userName;
	private Long selectedProjectId;
	private Long lastOpenedProjectId;
	private String sessionId;

	public WorkbenchSessionInfo() {
		
	}
	
	public WorkbenchSessionInfo(Integer userId, String userName, Long selectedProjectId, Long lastOpenedProjectId, String sessionId) {
		this.userId = userId;
		this.userName = userName;
		this.selectedProjectId = selectedProjectId;
		this.lastOpenedProjectId = lastOpenedProjectId;
		this.sessionId = sessionId;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public Long getSelectedProjectId() {
		return selectedProjectId;
	}

	public Long getLastOpenedProjectId() {
		return lastOpenedProjectId;
	}

	public String getSessionId() {
		return sessionId;
	}

}