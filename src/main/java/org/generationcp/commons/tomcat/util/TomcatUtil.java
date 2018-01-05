
package org.generationcp.commons.tomcat.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatUtil {

	private String managerUrl;

	private String username;

	private String password;

	public TomcatUtil() {
	}

	public TomcatUtil(String managerUrl, String username, String password) {
		this.managerUrl = managerUrl;
		this.username = username;
		this.password = password;
	}

	public String getManagerUrl() {
		return this.managerUrl;
	}

	public void setManagerUrl(String managerUrl) {
		this.managerUrl = managerUrl;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public WebAppStatusInfo getWebAppStatus() throws IOException {
		WebAppStatusInfo statusInfo = new WebAppStatusInfo();

		String listOutput = this.doHttpGetRequest(this.managerUrl + "/list", null);
		String[] lines = listOutput.split("\n");

		if (lines.length > 0) {
			String line1 = lines[0];
			if (!line1.trim().startsWith("OK")) {
				return statusInfo;
			}
		}

		for (int lineIndex = 1; lineIndex < lines.length; lineIndex++) {
			String line = lines[lineIndex];
			String[] statusTokens = line.split(":");

			String contextPath = statusTokens[0].trim();
			String state = statusTokens[1].trim();
			String path = statusTokens[3].trim();

			WebAppStatus status = WebAppStatus.createStatus(contextPath, state, path);
			statusInfo.addStatus(contextPath, status);
		}

		return statusInfo;
	}

	public void deployLocalWar(String contextPath, String localWarPath) throws IOException {
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("path", contextPath);
		requestParams.put("war", "file:/" + localWarPath);

		String startOutput = this.doHttpGetRequest(this.managerUrl + "/deploy", requestParams);
		String[] lines = startOutput.split("\n");

		if (lines.length > 0) {
			String line1 = lines[0];
			if (!line1.trim().startsWith("OK")) {
				throw new IOException("Cannot start webapp " + contextPath);
			}
		}
	}

	public static String getContextPathFromUrl(String url) throws MalformedURLException {
		URL urlObj = new URL(url);
		String path = urlObj.getPath();

		String[] pathTokens = path.split("/");
		for (String str : pathTokens) {
			if (str.trim().length() > 0) {
				return "/" + str;
			}
		}

		return "/";
	}

	public static String getLocalWarPathFromUrl(String url) throws MalformedURLException {
		URL urlObj = new URL(url);
		String path = urlObj.getPath();

		String[] pathTokens = path.split("/");
		for (String str : pathTokens) {
			if (str.trim().length() > 0) {
				return str;
			}
		}

		return "/";
	}

	protected String doHttpGetRequest(String url, Map<String, String> requestParams) throws IOException {
		URL urlObj = new URL(url);

		HttpClient client = new HttpClient();
		Credentials credentials = new UsernamePasswordCredentials(this.username, this.password);
		client.getState().setCredentials(new AuthScope(urlObj.getHost(), urlObj.getPort()), credentials);

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		if (requestParams != null) {
			for (String key : requestParams.keySet()) {
				paramList.add(new NameValuePair(key, requestParams.get(key)));
			}
		}

		GetMethod method = new GetMethod(url);
		method.setDoAuthentication(true);
		method.setQueryString(paramList.toArray(new NameValuePair[0]));

		int status = client.executeMethod(method);

		byte[] responseBody = method.getResponseBody();
		return status == HttpStatus.SC_OK ? new String(responseBody) : "";
	}

}
