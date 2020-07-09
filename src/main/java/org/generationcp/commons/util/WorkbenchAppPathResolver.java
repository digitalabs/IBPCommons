
package org.generationcp.commons.util;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by cyrus on 3/6/15.
 */
public class WorkbenchAppPathResolver {

	public static String getFullWebAddress(String url) {
		return WorkbenchAppPathResolver.getFullWebAddress(url, "");
	}

	public static String getFullWebAddress(String url, String param) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String bms_scheme = System.getenv("BMS_SCHEME");
		String bms_port = System.getenv("BMS_PORT");
		String paramFormat = !param.isEmpty() ? "?%s" : "";
		String urlFormat = "%s://%s:%d/%s" + paramFormat;

		String scheme = (bms_scheme == null || bms_scheme.length() == 0) ? request.getScheme() : bms_scheme;
		String serverName = request.getServerName();
		int port = (bms_port == null || bms_port.length() == 0) ? request.getServerPort() : Integer.parseInt(bms_port);

		url = '/' == url.charAt(0) ? url.substring(1) : url;
		param = param.startsWith("?") | param.startsWith("&") ? param.substring(1) : param;

		return !url.startsWith("http") ? String.format(urlFormat, scheme, serverName, port, url, param) : String.format("%s" + paramFormat,
				url, param);
	}

	public static String getWorkbenchAppPath(Tool tool, String idParam) {
		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, idParam, "");
	}

	public static String getWorkbenchAppPath(Tool tool, Integer idParam) {
		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(idParam), "");
	}

	public static String getWorkbenchAppPath(Tool tool, String idParam, String addtlParam) {
		String appPath = tool.getPath();

		// make sure no trailing slash in url path
		appPath = appPath.endsWith("/") ? appPath.substring(0, appPath.length() - 1) : appPath;

		if (!"null".equals(idParam) && !StringUtil.isEmptyOrWhitespaceOnly(idParam)) {
			if (ToolName.STUDY_MANAGER_FIELDBOOK_WEB.getName().equals(tool.getToolName())) {
				appPath += "/openTrial/";
			} else if (tool.getToolName().equals(ToolName.GERMPLASM_BROWSER.getName()) ||
				tool.getToolName().equals(ToolName.STUDY_BROWSER_WITH_ID.getName())) {
				appPath += "-";
			}

			appPath += idParam;
		}

		if (Util.isOneOf(tool.getToolType(), ToolType.WEB, ToolType.WEB_WITH_LOGIN)) {
			appPath = WorkbenchAppPathResolver.getFullWebAddress(appPath, addtlParam);
		}

		return appPath;
	}
}
