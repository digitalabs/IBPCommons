package org.generationcp.commons.util;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
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


public class ContextUtilTest {

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private HttpServletRequest request;
	
	@Mock
	private HttpSession session;
	
	private static Project testProject;
	
	@BeforeClass
	public static void setupOnce() {
		testProject = new Project();
		testProject.setProjectId(1L);
		testProject.setProjectName("Rice Breeding Programme");
		testProject.setLocalDbName("ibdbv2_rice_local");
		testProject.setCentralDbName("ibdbv2_rice_central");
	}

	@Before
	public void setUpEach() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetProjectInContextResolvesFromSessionContext() throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO)).thenReturn(new ContextInfo(1, 1L));
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);	
		when(this.workbenchDataManager.getProjectById(1L)).thenReturn(testProject);
		
		Assert.assertNotNull(ContextUtil.getProjectInContext(this.workbenchDataManager, this.request));
		verify(this.workbenchDataManager).getProjectById(Matchers.anyLong());
		verify(this.workbenchDataManager, never()).getLastOpenedProjectAnyUser();
	}
	
	@Test
	public void testGetProjectInContextFallsBackToOldMethod() throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO)).thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);	
		when(this.workbenchDataManager.getLastOpenedProjectAnyUser()).thenReturn(testProject);
		
		Assert.assertNotNull(ContextUtil.getProjectInContext(this.workbenchDataManager, this.request));
		verify(this.workbenchDataManager).getLastOpenedProjectAnyUser();
		verify(this.workbenchDataManager, never()).getProjectById(Matchers.anyLong());
	}
	
	@Test
	public void testCurrentWorkbenchUserIdResolvesFromSessionContext() throws MiddlewareQueryException {
		ContextInfo contextInfo = new ContextInfo(1, 1L);
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO)).thenReturn(contextInfo);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);	
		
		Assert.assertEquals(contextInfo.getloggedInUserId(), ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request));
		
		verify(this.workbenchDataManager, never()).getWorkbenchRuntimeData();
	}
	
	@Test
	public void testCurrentWorkbenchUserIdFallsBackToOldMethod() throws MiddlewareQueryException {
		when(this.session.getAttribute(ContextConstants.SESSION_ATTR_CONTEXT_INFO)).thenReturn(null);
		when(this.request.getSession(Matchers.anyBoolean())).thenReturn(this.session);
		WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
		workbenchRuntimeData.setUserId(1);
		when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(workbenchRuntimeData);
		
		Assert.assertEquals(workbenchRuntimeData.getUserId(), ContextUtil.getCurrentWorkbenchUserId(this.workbenchDataManager, this.request));
		
		verify(this.workbenchDataManager).getWorkbenchRuntimeData();
	}

}
