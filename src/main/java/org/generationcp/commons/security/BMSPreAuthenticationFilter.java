package org.generationcp.commons.security;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;


public class BMSPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String authToken = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);
		if(authToken != null) {
			String principal = SecurityUtil.decodeToken(authToken);
			return principal;
		}
		return null;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "";
	}

}
