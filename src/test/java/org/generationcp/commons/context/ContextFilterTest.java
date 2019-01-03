package org.generationcp.commons.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RunWith(MockitoJUnitRunner.class)
public class ContextFilterTest {

	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	HttpServletResponse httpServletResponse;

	@Mock
	HttpSession httpSession;

	@Mock
	FilterChain filterChain;

	ContextFilter contextFilter;

	@Before
	public void setup(){
		contextFilter = new ContextFilter();
	}

	@Test
	public void testDoFilterWithContextInfoInRequestParams() throws Exception{
		final String contextPath = "/contextPath";
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn(contextPath);

		Mockito.when(httpServletRequest.getParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID)).thenReturn("1");
		Mockito.when(httpServletRequest.getParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID)).thenReturn("1");
		Mockito.when(httpServletRequest.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn("authToken");
		Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);
		Mockito.when(httpServletRequest.getContextPath()).thenReturn(contextPath);

		contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		ContextInfo expectedContextInfo = new ContextInfo(1, 1L, "authToken");

		Cookie expectedUserIdCookie = this.getCookie(ContextConstants.PARAM_LOGGED_IN_USER_ID, "1", contextPath);
		Cookie expectedProjectIdCookie = this.getCookie(ContextConstants.PARAM_SELECTED_PROJECT_ID, "1", contextPath);
		Cookie expectedAuthTokenCookie = this.getCookie(ContextConstants.PARAM_AUTH_TOKEN, "authToken", contextPath);

		Mockito.verify(httpSession).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), Matchers.refEq(expectedContextInfo));
		Mockito.verify(httpServletResponse).addCookie(Matchers.refEq(expectedUserIdCookie));
		Mockito.verify(httpServletResponse).addCookie(Matchers.refEq(expectedProjectIdCookie));
		Mockito.verify(httpServletResponse).addCookie(Matchers.refEq(expectedAuthTokenCookie));
		Mockito.verify(filterChain).doFilter(Matchers.refEq(httpServletRequest),Matchers.refEq(httpServletResponse));

	}

	@Test
	public void testDoFilterWithContextInfoNotInRequestParamsAndNotInSessionAttributeButInCookie() throws Exception{
		final String contextPath = "/contextPath";
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn(contextPath);
		Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);

		Cookie[] requestCookies = new Cookie[3];
		requestCookies[0] = this.getCookie(ContextConstants.PARAM_LOGGED_IN_USER_ID, "11", contextPath);
		requestCookies[1] = this.getCookie(ContextConstants.PARAM_SELECTED_PROJECT_ID, "12", contextPath);
		requestCookies[2] = this.getCookie(ContextConstants.PARAM_AUTH_TOKEN, "AuthenticationToken", contextPath);

		Mockito.when(httpServletRequest.getCookies()).thenReturn(requestCookies);

		contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		ContextInfo expectedContextInfo = new ContextInfo(11, 12L, "AuthenticationToken");
		Mockito.verify(httpSession).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), Matchers.refEq(expectedContextInfo));
		Mockito.verify(filterChain).doFilter(Matchers.refEq(httpServletRequest),Matchers.refEq(httpServletResponse));

	}

	@Test
	public void testDoFilterWithContextInfoNotInRequestParamsAndNotInSessionAttributeAndNotInCookie() throws Exception{
		final String contextPath = "/contextPath";
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn(contextPath);

		Cookie[] requestCookies = null;
		Mockito.when(httpServletRequest.getCookies()).thenReturn(requestCookies);

		contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		Mockito.verify(httpSession, Mockito.never()).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), Matchers.anyObject());
		Mockito.verify(filterChain).doFilter(Matchers.refEq(httpServletRequest),Matchers.refEq(httpServletResponse));

	}

	@Test
	public void testDoFilterWithContextInfoNotInRequestParamButInSessionAttribute() throws Exception{
		final String contextPath = "/contextPath";
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn(contextPath);

		ContextInfo sessionContextInfo = new ContextInfo(11, 12L, "AuthenticationToken");

		contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		Mockito.verify(httpSession, Mockito.never()).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), Matchers.anyObject());
		Mockito.verify(filterChain).doFilter(Matchers.refEq(httpServletRequest),Matchers.refEq(httpServletResponse));

	}

	@Test
	public void testDoFilterWithStaticResource() throws Exception{
		final String contextPath = "/static";
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn(contextPath);

		contextFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		Mockito.verify(filterChain).doFilter(Matchers.refEq(httpServletRequest),Matchers.refEq(httpServletResponse));
	}


	private Cookie getCookie(String cookieName, String cookieValue , String cookiePath){
		Cookie cookie = new Cookie(cookieName , cookieValue);
		cookie.setPath(cookiePath);

		return cookie;
	}

}
