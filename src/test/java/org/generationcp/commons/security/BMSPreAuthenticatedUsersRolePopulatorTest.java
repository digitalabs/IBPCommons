package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;


public class BMSPreAuthenticatedUsersRolePopulatorTest {
	
	private static final String TEST_USER = "testUser";
	private WorkbenchDataManager workbenchDataManager;
	private BMSPreAuthenticatedUsersRolePopulator rolesPopulator;
	
	private HttpServletRequest request;
	
	@Before
	public void setUpPerTest() {
		workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		rolesPopulator = new BMSPreAuthenticatedUsersRolePopulator();
		rolesPopulator.setWorkbenchDataManager(workbenchDataManager);
		request = Mockito.mock(HttpServletRequest.class);
	}
	
	@Test
	public void testBuildDetails() {
		try {
			Mockito.when(request.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn(TEST_USER);
			rolesPopulator.buildDetails(request);
			
			List<User> matchingUsers = new ArrayList<User>();
			User testUserWorkbench = new User();
			testUserWorkbench.setName(TEST_USER);
			testUserWorkbench.setPassword("password");
			UserRole testUserRole = new UserRole(testUserWorkbench, "ADMIN");
			testUserWorkbench.setRoles(Arrays.asList(testUserRole));
			matchingUsers.add(testUserWorkbench);

			Mockito.when(workbenchDataManager.getUserByName(TEST_USER, 0, 1, Operation.EQUAL)).thenReturn(matchingUsers);
			
			PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails roleDetails =
					(PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails) rolesPopulator.buildDetails(request);
			Assert.assertEquals(testUserWorkbench.getRoles().size(), roleDetails.getGrantedAuthorities().size());
			Assert.assertEquals(SecurityUtil.ROLE_PREFIX + testUserRole.getRole(), roleDetails.getGrantedAuthorities().get(0).getAuthority());
			
		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test(expected = AuthenticationServiceException.class)
	public void testLoadUserDataAccessError() throws MiddlewareQueryException {
		Mockito.when(request.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn(TEST_USER);
		Mockito.when(workbenchDataManager.getUserByName(TEST_USER, 0, 1, Operation.EQUAL)).thenThrow(new MiddlewareQueryException("Boom!"));
		rolesPopulator.buildDetails(request);
	}

}
