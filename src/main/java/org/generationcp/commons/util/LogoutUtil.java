package org.generationcp.commons.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutUtil {

	public static void manuallyLogout(HttpServletRequest request, HttpServletResponse response){
		HttpSession context = request.getSession(false);
		if(context != null) {
			context.invalidate();
		}

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication((Authentication)null);

		for(Cookie requestCookie : request.getCookies()) {
			Cookie cookie = new Cookie(requestCookie.getName(), (String)null);
			cookie.setMaxAge(0);
			cookie.setPath(getCookiePath(request));
			response.addCookie(cookie);
		}

		SecurityContextHolder.clearContext();
	}

	private static String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

}
