package org.generationcp.commons.util;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtilTest {
	
	private final Logger LOG = LoggerFactory.getLogger(UserUtilTest.class);
	private WorkbenchDataManager workbenchDataManager;
	private Integer EXPECTED_USER_ID = (int) (Math.random() * 100);
	
	@Before
	public void setUp(){
		workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		
		WorkbenchRuntimeData runtimeDate = new WorkbenchRuntimeData();
		runtimeDate.setUserId(new Integer(5));

		Project dummyProject = new Project();
		dummyProject.setProjectId(new Long(5));

		try {
			Mockito.when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(
					runtimeDate);
			Mockito.when(this.workbenchDataManager.getLastOpenedProject(runtimeDate.getUserId()))
					.thenReturn(dummyProject);
			Mockito.when(
					this.workbenchDataManager.getLocalIbdbUserId(runtimeDate.getUserId(),
							dummyProject.getProjectId())).thenReturn(EXPECTED_USER_ID);

		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
			Assert.fail("Failed to create an ibdbuser instance.");
		}

		try {
			Mockito.when(UserUtil.getCurrentUserLocalId(this.workbenchDataManager)).thenReturn(EXPECTED_USER_ID);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
			Assert.fail("Failed to create a ibdbuser data.");
		}
	}
	
	@Test
	public void testGetCurrentUserLocalId(){
		Integer userId = -1;
		try {
			userId = UserUtil.getCurrentUserLocalId(workbenchDataManager);
		} catch (MiddlewareQueryException e) {
			Assert.fail("Expecting a local ID returned but didn't.");
		}
		
		Assert.assertNotSame("Expecting an invalid user local ID returned but didn't.", userId, new Integer(-1));
		
		Assert.assertSame("Expecting a valid user local ID returned.", userId, EXPECTED_USER_ID);
	}
}
