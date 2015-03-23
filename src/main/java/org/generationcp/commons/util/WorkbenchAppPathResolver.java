package org.generationcp.commons.util;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cyrus on 3/6/15.
 */
public class WorkbenchAppPathResolver {

	public static String getFullWebAddress(String url) {
		return getFullWebAddress(url,"");
	}

	public static String getFullWebAddress(String url, String param) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String urlFormat = "%s://%s:%d/%s" + (!param.isEmpty() ? "?%s" : "");

		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();

		param = param.startsWith("?") | param.startsWith("&") ? param.substring(1) : param;

		return String.format(urlFormat, scheme, serverName, port, url,param);
	}

	public static String getWorkbenchAppPath(Tool tool, String idParam)
	{
		return getWorkbenchAppPath(tool,idParam,"");
	}


	public static String getWorkbenchAppPath(Tool tool, Integer idParam)
	{
		return getWorkbenchAppPath(tool,String.valueOf(idParam),"");
	}

	public static String getWorkbenchAppPath(Tool tool, String idParam,String addtlParam) {
		String appPath = tool.getPath();

		// make sure no trailing slash in url path
		appPath = appPath.endsWith("/") ? appPath.substring(0, appPath.length() - 1) : appPath;

		if (!"null".equals(idParam) && !StringUtil.isEmptyOrWhitespaceOnly(idParam)) {
			if (ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.getToolName().equals(tool.getToolName())) {
				appPath += "/openTrial/";
			} else if (ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB.getToolName().equals(
					tool.getToolName())) {
				appPath += "/editNursery/";
			} else if (Util.isOneOf(tool.getToolName(), ToolEnum.BM_LIST_MANAGER.getToolName(),
					ToolEnum.STUDY_BROWSER_WITH_ID.getToolName(),
					ToolEnum.GERMPLASM_BROWSER.getToolName(),
					ToolEnum.GERMPLASM_LIST_BROWSER)) {
				appPath += "-";
			}

			appPath += idParam;
		}

		if (Util.isOneOf(tool.getToolType(), ToolType.WEB, ToolType.WEB_WITH_LOGIN)) {
			appPath = getFullWebAddress(appPath,addtlParam);
		}

		return appPath;
	}
}
