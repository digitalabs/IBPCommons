package org.generationcp.commons.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.generationcp.commons.util.ContextUtil;
import org.springframework.web.util.WebUtils;

public class ContextFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//NOOP
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		Long selectedProjectId = ContextUtil.getParamAsLong(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
		Integer userId = ContextUtil.getParamAsInt(request, ContextConstants.PARAM_LOGGED_IN_USER_ID); 
		
		if (selectedProjectId != null && userId != null) {
			WebUtils.setSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO, new ContextInfo(userId, selectedProjectId));
			response.addCookie(new Cookie(ContextConstants.PARAM_LOGGED_IN_USER_ID, userId.toString()));
			response.addCookie(new Cookie(ContextConstants.PARAM_SELECTED_PROJECT_ID, selectedProjectId.toString()));
		}
		
		else {
			ContextInfo contextInfo = (ContextInfo) WebUtils.getSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO);
			
			if(contextInfo == null) {
				//this happens when session attribute gets lost due to session.invalidate() calls when navigating within application.
				//restore session attribure from cookies
				Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
				Cookie selectedProjectIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_SELECTED_PROJECT_ID);
				if(userIdCookie != null && selectedProjectIdCookie != null) {
					WebUtils.setSessionAttribute(request, ContextConstants.SESSION_ATTR_CONTEXT_INFO, 
						new ContextInfo(Integer.valueOf(userIdCookie.getValue()), Long.valueOf(selectedProjectIdCookie.getValue())));
				}
			}
		}
		
		chain.doFilter(request, response);	
	}

	@Override
	public void destroy() {
		//NOOP
	}
    
}
