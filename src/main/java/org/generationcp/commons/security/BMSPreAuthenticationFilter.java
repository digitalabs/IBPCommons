package org.generationcp.commons.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class BMSPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
	private static final Logger LOG = LoggerFactory.getLogger(BMSPreAuthenticationFilter.class);
	@Resource
	WorkbenchDataManager workbenchDataManager;

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		try {
			return ContextUtil.getCurrentWorkbenchUsername(workbenchDataManager, request);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "";
	}

}
