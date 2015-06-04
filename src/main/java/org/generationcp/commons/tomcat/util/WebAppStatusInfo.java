
package org.generationcp.commons.tomcat.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.tomcat.util.WebAppStatus.State;

public class WebAppStatusInfo {

	private final Map<String, WebAppStatus> webAppStatus = new LinkedHashMap<String, WebAppStatus>();

	public void addStatus(String contextPath, WebAppStatus status) {
		this.webAppStatus.put(contextPath, status);
	}

	public boolean isDeployed(String contextPath) {
		WebAppStatus status = this.webAppStatus.get(contextPath);
		return status != null;
	}

	public boolean isRunning(String contextPath) {
		WebAppStatus status = this.webAppStatus.get(contextPath);
		return status == null ? false : status.getState() == State.RUNNING;
	}

	public List<WebAppStatus> asList() {
		return new ArrayList<WebAppStatus>(this.webAppStatus.values());
	}
}
