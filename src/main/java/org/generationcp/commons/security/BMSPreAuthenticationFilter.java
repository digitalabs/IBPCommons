
package org.generationcp.commons.security;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class BMSPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(BMSPreAuthenticationFilter.class);

	@Override
	protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
		/**
		 * Pre-authenticate IF AND ONLY IF there is authToken provided. No other fall backs such as use of ContextUtil should be allowed. We
		 * must reject all requests where no authToken parameter is present. Returning null ensures this happens.
		 * 
		 * WARNING for newbie developers : DO NOT change any logic here else a big security hole will be opened up. Contact a senior
		 * developer in Team NZ before making any changes to this class.
		 */
		String authToken = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);
		if (authToken != null) {
			String principal = SecurityUtil.decodeToken(authToken);
			return principal;
		}
		LOG.info("Could not pre-authenticate the request: {}. No authentication token was supplied.", request.getRequestURL());
		return null;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "";
	}

}
