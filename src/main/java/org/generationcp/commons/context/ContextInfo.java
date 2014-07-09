package org.generationcp.commons.context;

/**
 * POJO used to expose context information (typically from the Workbench).
 * 
 * @author Naymesh Mistry
 */
public class ContextInfo {
	
	private final Integer loggedInUserId; 
	private final Long selectedProjectId;
		
	public ContextInfo(Integer loggedInUserId, Long selectedProjectId) {
		this.loggedInUserId = loggedInUserId;
		this.selectedProjectId = selectedProjectId;
	}

	public Integer getloggedInUserId() {
		return loggedInUserId;
	}

	public Long getSelectedProjectId() {
		return selectedProjectId;
	}
}