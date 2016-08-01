
package org.generationcp.commons.context;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		
		if (!(o instanceof ContextInfo)) {
			return false;
		}
		
		final ContextInfo other = (ContextInfo) o;
		return Objects.equals(this.loggedInUserId, other.loggedInUserId)
				&& Objects.equals(this.loggedInUserId, other.selectedProjectId)
				&& Objects.equals(this.authToken, other.authToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.loggedInUserId, this.loggedInUserId, this.authToken);
	}
}
