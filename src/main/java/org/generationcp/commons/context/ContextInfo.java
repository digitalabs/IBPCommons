package org.generationcp.commons.context;

/**
 * POJO used to expose context information (typically from the Workbench).
 * 
 * @author Naymesh Mistry
 */
public class ContextInfo {
	
	private final Long loggedInUserId; 
	private final Long selectedProjectId;
		
	public ContextInfo(Long loggedInUserId, Long selectedProjectId) {
		this.loggedInUserId = loggedInUserId;
		this.selectedProjectId = selectedProjectId;
	}

	public Long getloggedInUserId() {
		return loggedInUserId;
	}

	public Long getSelectedProjectId() {
		return selectedProjectId;
	}
}