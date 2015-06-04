
package org.generationcp.commons.context;

/**
 * POJO used to expose context information (typically from the Workbench).
 *
 * @author Naymesh Mistry
 */
public class ContextInfo {

	private final Integer loggedInUserId;
	private final Long selectedProjectId;
	private final String authToken;

	public ContextInfo(Integer userId, Long selectedProjectId, String authToken) {
		this.loggedInUserId = userId;
		this.selectedProjectId = selectedProjectId;
		this.authToken = authToken;
	}

	public ContextInfo(Integer userId, Long selectedProjectId) {
		this(userId, selectedProjectId, "");
	}

	public Integer getloggedInUserId() {
		return this.loggedInUserId;
	}

	public Long getSelectedProjectId() {
		return this.selectedProjectId;
	}

	public String getAuthToken() {
		return this.authToken;
	}

}
