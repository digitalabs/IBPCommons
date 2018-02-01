package org.generationcp.commons.tomcat.util;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TomcatUtil {

	private String managerUrl;

	private String username;

	private String password;

	public TomcatUtil() {
	}

	public TomcatUtil(final String managerUrl, final String username, final String password) {
		this.managerUrl = managerUrl;
		this.username = username;
		this.password = password;
	}

	public String getManagerUrl() {
		return this.managerUrl;
	}

	public void setManagerUrl(final String managerUrl) {
		this.managerUrl = managerUrl;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public WebAppStatusInfo getWebAppStatus() throws IOException {
		final WebAppStatusInfo statusInfo = new WebAppStatusInfo();

		final String listOutput = this.doHttpGetRequest(this.managerUrl + "/list", null);
		final String[] lines = listOutput.split("\n");

		if (lines.length > 0) {
			final String line1 = lines[0];
			if (!line1.trim().startsWith("OK")) {
				return statusInfo;
			}
		}

		for (int lineIndex = 1; lineIndex < lines.length; lineIndex++) {
			final String line = lines[lineIndex];
			final String[] statusTokens = line.split(":");

			final String contextPath = statusTokens[0].trim();
			final String state = statusTokens[1].trim();
			final String path = statusTokens[3].trim();

			final WebAppStatus status = WebAppStatus.createStatus(contextPath, state, path);
			statusInfo.addStatus(contextPath, status);
		}

		return statusInfo;
	}

	public void deployLocalWar(final String contextPath, final String localWarPath) throws IOException {
		final Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("path", contextPath);
		requestParams.put("war", "file:/" + localWarPath);

		final String startOutput = this.doHttpGetRequest(this.managerUrl + "/deploy", requestParams);
		final String[] lines = startOutput.split("\n");

		if (lines.length > 0) {
			final String line1 = lines[0];
			if (!line1.trim().startsWith("OK")) {
				throw new IOException("Cannot start webapp " + contextPath);
			}
		}
	}

	public static String getContextPathFromUrl(final String url) throws MalformedURLException {
		final URL urlObj = new URL(url);
		final String path = urlObj.getPath();

		final String[] pathTokens = path.split("/");
		for (final String str : pathTokens) {
			if (str.trim().length() > 0) {
				return "/" + str;
			}
		}

		return "/";
	}

	public static String getLocalWarPathFromUrl(final String url) throws MalformedURLException {
		final URL urlObj = new URL(url);
		final String path = urlObj.getPath();

		final String[] pathTokens = path.split("/");
		for (final String str : pathTokens) {
			if (str.trim().length() > 0) {
				return str;
			}
		}

		return "/";
	}

	protected String doHttpGetRequest(final String url, final Map<String, String> requestParams) throws IOException {
		final URL urlObj = new URL(url);

		final HttpClient client = new HttpClient();
		final Credentials credentials = new UsernamePasswordCredentials(this.username, this.password);
		client.getState().setCredentials(new AuthScope(urlObj.getHost(), urlObj.getPort()), credentials);

		final List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		if (requestParams != null) {
			for (final String key : requestParams.keySet()) {
				paramList.add(new NameValuePair(key, requestParams.get(key)));
			}
		}

		final GetMethod method = new GetMethod(url);
		method.setDoAuthentication(true);
		method.setQueryString(paramList.toArray(new NameValuePair[0]));

		final int status = client.executeMethod(method);

		final byte[] responseBody = method.getResponseBody();
		return status == HttpStatus.SC_OK ? new String(responseBody) : "";
	}

}
