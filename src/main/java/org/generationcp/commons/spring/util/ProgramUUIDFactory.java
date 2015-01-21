package org.generationcp.commons.spring.util;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 1/20/2015
 * Time: 5:14 PM
 */
public class ProgramUUIDFactory {

	/** The Constant LOG. */
	    private static final Logger LOG = LoggerFactory.getLogger(ProgramUUIDFactory.class);

	@Resource
	private HttpServletRequest request;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	public String getCurrentProgramUUID() {
		ContextInfo info = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);

		try {
			return workbenchDataManager.getProjectById(info.getSelectedProjectId()).getUniqueID();
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(),e);
		}

		return "";
	}
}