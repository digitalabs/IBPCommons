/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.SpringAppContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/2/13 Time: 1:31 PM To change this template use File | Settings | File Templates.
 */
public class HTTPRequestAwareServletFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(HTTPRequestAwareServletFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOOP
	}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
			ServletException {

		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse resp = (HttpServletResponse) servletResponse;

		String requestUri =
				String.format("%s:%s%s?%s", req.getServerName(), req.getServerPort(), req.getRequestURI(), req.getQueryString());

		if (!ContextUtil.isStaticResourceRequest(req.getRequestURI())) {
			HTTPRequestAwareServletFilter.LOG.trace("Request started @ " + requestUri);

			synchronized (this) {
				HttpRequestAwareUtil.onRequestStart(SpringAppContextProvider.getApplicationContext(), req, resp);
			}
			

			filterChain.doFilter(servletRequest, servletResponse);


			HTTPRequestAwareServletFilter.LOG.trace("Request ended @ " + requestUri);

			synchronized (this) {
				HttpRequestAwareUtil.onRequestEnd(SpringAppContextProvider.getApplicationContext(), req, resp);
			}
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
		// NOOP
	}
}
