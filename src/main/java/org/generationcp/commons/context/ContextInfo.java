package org.generationcp.commons.context;

/**
 * POJO used to expose context information (typically from the Workbench).
 * 
 * @author Naymesh Mistry
 */
public class ContextInfo {
	
	private final Long userId; 
	private final Long selectedProjectId;
	
	private String userName;
	private Long lastOpenedProjectId;
	private String workbenchSessionId;

	public ContextInfo(Long userId, Long selectedProjectId) {
		this.userId = userId;
		this.selectedProjectId = selectedProjectId;
	}
	
	public ContextInfo(Long userId, Long selectedProjectId, String userName, Long lastOpenedProjectId, String sessionId) {
		this.userId = userId;
		this.selectedProjectId = selectedProjectId;
		this.userName = userName;		
		this.lastOpenedProjectId = lastOpenedProjectId;
		this.workbenchSessionId = sessionId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getSelectedProjectId() {
		return selectedProjectId;
	}

	public Long getLastOpenedProjectId() {
		return lastOpenedProjectId;
	}

	public void setLastOpenedProjectId(Long lastOpenedProjectId) {
		this.lastOpenedProjectId = lastOpenedProjectId;
	}

	public String getWorkbenchSessionId() {
		return workbenchSessionId;
	}

	public void setWorkbenchSessionId(String workbenchSessionId) {
		this.workbenchSessionId = workbenchSessionId;
	}
}