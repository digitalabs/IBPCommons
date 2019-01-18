package org.generationcp.commons.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RunWith(MockitoJUnitRunner.class)
public class LogoutUtilTest {

	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	HttpServletResponse httpServletResponse;

	@Mock HttpSession httpSession;


	@Test
	public void testManuallyLogoutSuccessWithSession() throws Exception{
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);

		Cookie[] requestCookies = new Cookie[1];
		Cookie cookie = new Cookie("cookieName", "cookieValue");
		cookie.setPath("contextPath");
		requestCookies[0] = cookie;

		Mockito.when(httpServletRequest.getCookies()).thenReturn(requestCookies);
		Mockito.when(httpServletRequest.getContextPath()).thenReturn("contextPath");

		LogoutUtil.manuallyLogout(httpServletRequest, httpServletResponse);

		Cookie expectedCookie = new Cookie("cookieName", null);
		expectedCookie.setPath("contextPath");
		expectedCookie.setMaxAge(0);

		Mockito.verify(httpSession).invalidate();
		Mockito.verify(httpServletResponse).addCookie(Matchers.refEq(expectedCookie));
	}

	@Test
	public void testManuallyLogoutSuccessWithNoSession() throws Exception{
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(null);

		Cookie[] requestCookies = new Cookie[1];
		Cookie cookie = new Cookie("cookieName", "cookieValue");
		cookie.setPath("contextPath");
		requestCookies[0] = cookie;

		Mockito.when(httpServletRequest.getCookies()).thenReturn(requestCookies);
		Mockito.when(httpServletRequest.getContextPath()).thenReturn("contextPath");

		LogoutUtil.manuallyLogout(httpServletRequest, httpServletResponse);

		Cookie expectedCookie = new Cookie("cookieName", null);
		expectedCookie.setPath("contextPath");
		expectedCookie.setMaxAge(0);

		Mockito.verify(httpSession, Mockito.never()).invalidate();
		Mockito.verify(httpServletResponse).addCookie(Matchers.refEq(expectedCookie));
	}


}
