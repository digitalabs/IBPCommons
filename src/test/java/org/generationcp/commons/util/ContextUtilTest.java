package org.generationcp.commons.util;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class ContextUtilTest {

	private static final String SAMPLE_AUTH_TOKEN = "RANDOM_TOKEN";
	private static Project testProject;
	@Mock
	private WorkbenchDataManager workbenchDataManager;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpSession session;

	@BeforeClass
	public static void setupOnce() {
		testProject = new Project();
		testProject.setProjectId(1L);
		testProject.setProjectName("Rice Breeding Programme");
	}

	@Before
	public void setUpEach() {
		MockitoAnnotations.initMocks(this);


		when(request.getParameter(ContextConstants.PARAM_LOGGED_IN_USER_ID)).thenReturn("1");
		when(request.getParameter(ContextConstants.PARAM_SELECTED_PROJECT_ID)).thenReturn("1");
		when(request.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn(SAMPLE_AUTH_TOKEN);
	}

	@Test
	public void testGetProjectInContextResolvesFromSessionContext()
			throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(new ContextInfo(1, 1L));
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		when(this.workbenchDataManager.getProjectById(1L)).thenReturn(testProject);

		Assert.assertNotNull(
				ContextUtil.getProjectInContext(this.workbenchDataManager, this.request));
		verify(this.workbenchDataManager).getProjectById(Matchers.anyLong());
		verify(this.workbenchDataManager, never()).getLastOpenedProjectAnyUser();
	}

	@Test
	public void testGetProjectInContextFallsBackToOldMethod() throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		when(this.workbenchDataManager.getLastOpenedProjectAnyUser()).thenReturn(testProject);

		Assert.assertNotNull(
				ContextUtil.getProjectInContext(this.workbenchDataManager, this.request));
		verify(this.workbenchDataManager).getLastOpenedProjectAnyUser();
		verify(this.workbenchDataManager, never()).getProjectById(Matchers.anyLong());
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testExceptionIsThrownWhenProjectCannotBeResolved() throws MiddlewareQueryException {

		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		when(this.workbenchDataManager.getLastOpenedProjectAnyUser()).thenReturn(null);

		ContextUtil.getProjectInContext(this.workbenchDataManager, this.request);
	}

	@Test
	public void testCurrentWorkbenchUserIdResolvesFromSessionContext()
			throws MiddlewareQueryException {
		ContextInfo contextInfo = new ContextInfo(1, 1L);
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(contextInfo);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);

		Assert.assertEquals(contextInfo.getloggedInUserId(),
				ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request));

		verify(this.workbenchDataManager, never()).getWorkbenchRuntimeData();
	}

	@Test
	public void testCurrentWorkbenchUserIdFallsBackToOldMethod() throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
		workbenchRuntimeData.setUserId(1);
		when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(workbenchRuntimeData);

		Assert.assertEquals(workbenchRuntimeData.getUserId(),
				ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request));

		verify(this.workbenchDataManager).getWorkbenchRuntimeData();
	}

	@Test(expected = MiddlewareQueryException.class)
	public void testExceptionIsThrownWhenWorkbenchUserCannotBeResolved()
			throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
		workbenchRuntimeData.setUserId(null);
		when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(workbenchRuntimeData);

		ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request);
	}

	@Test
	public void testIsStaticResourceRequest() {

		Assert.assertFalse(ContextUtil.isStaticResourceRequest("/App/NonStaticResource"));

		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/static/app.whatever"));

		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/js/app.js"));
		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/css/app.css"));
		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/img/app.png"));
		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/img/app.gif"));
		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/img/app.jpg"));
		Assert.assertTrue(ContextUtil.isStaticResourceRequest("/App/font/app.woff"));
	}

	@Test
	public void testGetContextInfoFromRequest() throws Exception {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(new ContextInfo(1, 1L, SAMPLE_AUTH_TOKEN));
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		when(this.workbenchDataManager.getProjectById(1L)).thenReturn(testProject);

		Assert.assertNotNull(ContextUtil.getContextInfoFromRequest(request));
		Assert.assertEquals(Integer.valueOf(1),
				ContextUtil.getContextInfoFromRequest(request).getloggedInUserId());
		Assert.assertEquals(Long.valueOf(1L),ContextUtil.getContextInfoFromRequest(request).getSelectedProjectId());
		Assert.assertEquals(SAMPLE_AUTH_TOKEN,ContextUtil.getContextInfoFromRequest(request).getAuthToken());
	}

	@Test
	public void testGetCurrentWorkbenchUsername() throws Exception {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO))
				.thenReturn(new ContextInfo(1, 1L, SAMPLE_AUTH_TOKEN));
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		when(this.workbenchDataManager.getProjectById(1L)).thenReturn(testProject);

		Assert.assertEquals(SecurityUtil.decodeToken(SAMPLE_AUTH_TOKEN),ContextUtil.getCurrentWorkbenchUsername(workbenchDataManager,request));

	}
}
