package org.generationcp.commons.util;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.*;

/**
 * Unit test for the path resolver used in app switching
 */
public class WorkbenchAppPathResolverTest {

	private MockHttpServletRequest request;

	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	public void testGetFullWebAddress() throws Exception {
		// setup host + port
		final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		final HttpServletRequest request = requestAttributes.getRequest();
		final String scheme = request.getScheme();
		final String serverName = request.getServerName();
		final int port = request.getServerPort();

		final String hostPort = scheme + "://" + serverName + ":" + port +"/";
		final String testUrlNoSlash = "webapp/path";
		final String testUrlStartsWithSlash = "/" + testUrlNoSlash;

		final String testParam = "param=value";
		final String testParamStartsWithQuestion = "?" + testParam;
		final String testParamStartsWithAnd = "&" + testParam;

		Assert.assertEquals("Should return the full path", hostPort + testUrlNoSlash,WorkbenchAppPathResolver.getFullWebAddress(testUrlStartsWithSlash));
		Assert.assertEquals("Should return a correct address with no double slash after the port", hostPort + testUrlNoSlash,WorkbenchAppPathResolver.getFullWebAddress(testUrlStartsWithSlash));
		Assert.assertEquals("Should return the full path with default param",hostPort + testUrlNoSlash + "?" + testParam,WorkbenchAppPathResolver.getFullWebAddress(testUrlNoSlash,testParam));
		Assert.assertEquals("Should return correct address with no double question symbol",hostPort + testUrlNoSlash + "?" + testParam,WorkbenchAppPathResolver.getFullWebAddress(testUrlNoSlash,testParamStartsWithQuestion));
		Assert.assertEquals("Should return the full path with no & symbol at param start",hostPort + testUrlNoSlash + "?" + testParam,WorkbenchAppPathResolver.getFullWebAddress(testUrlNoSlash,testParamStartsWithAnd));

	}
}
