
package org.generationcp.commons.tomcat.util;

public class WebAppStatus {

	public static enum State {
		NOT_RUNNING, RUNNING
	}

	private String contextPath;
	private State state;
	private String path;

	public WebAppStatus(String contextPath, State state, String path) {
		this.contextPath = contextPath;
		this.state = state;
		this.path = path;
	}

	public String getContextPath() {
		return this.contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public State getState() {
		return this.state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isRunning() {
		return this.state == State.RUNNING;
	}

	public static WebAppStatus createStatus(String contextPath, String state, String path) {
		if (state != null && state.equals("running")) {
			return new WebAppStatus(contextPath, State.RUNNING, path);
		} else {
			return new WebAppStatus(contextPath, State.NOT_RUNNING, path);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(WebAppStatus.class.getName());
		sb.append("{");
		sb.append("context path=");
		sb.append(this.contextPath);
		sb.append(", ");
		sb.append("state=");
		sb.append(this.state);
		sb.append(", ");
		sb.append("path=");
		sb.append(this.path);
		sb.append("}");
		return sb.toString();
	}
}
