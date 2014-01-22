/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.hibernate;

import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.util.SpringAppContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/2/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestAwareServletFilter implements Filter {
    private final static Logger LOG = LoggerFactory.getLogger(HTTPRequestAwareServletFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;

        String request_uri = String.format("%s:%s%s?%s", req.getServerName()
                , req.getServerPort(), req.getRequestURI(), req.getQueryString());

        LOG.trace("Request started @ " + request_uri);

        synchronized (this) {
            HttpRequestAwareUtil.onRequestStart(
                    SpringAppContextProvider.getApplicationContext(),req,resp);
        }

        filterChain.doFilter(servletRequest,servletResponse);

        LOG.trace("Request ended @ " + request_uri);

        synchronized (this) {
            HttpRequestAwareUtil.onRequestEnd(
                    SpringAppContextProvider.getApplicationContext(),req,resp);
        }

    }

    @Override
    public void destroy() {

    }
}
