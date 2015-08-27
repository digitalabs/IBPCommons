
package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
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
import org.springframework.transaction.PlatformTransactionManager;

public class BMSPreAuthenticatedUsersRolePopulatorTest {

	private static final String TEST_USER = "testUser";
	private static final String AUTH_TOKEN = Base64.encodeBase64URLSafeString(BMSPreAuthenticatedUsersRolePopulatorTest.TEST_USER
			.getBytes());

	private WorkbenchDataManager workbenchDataManager;
	private PlatformTransactionManager transactionManager;
	private BMSPreAuthenticatedUsersRolePopulator rolesPopulator;

	private HttpServletRequest request;

	@Before
	public void setUpPerTest() {
		this.request = Mockito.mock(HttpServletRequest.class);
		this.workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		this.transactionManager = Mockito.mock(PlatformTransactionManager.class);

		this.rolesPopulator = new BMSPreAuthenticatedUsersRolePopulator();
		this.rolesPopulator.setWorkbenchDataManager(this.workbenchDataManager);
		this.rolesPopulator.setTransactionManager(this.transactionManager);
	}

	@Test
	public void testBuildDetails() {
		try {
			Mockito.when(this.request.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn(
					BMSPreAuthenticatedUsersRolePopulatorTest.AUTH_TOKEN);
			this.rolesPopulator.buildDetails(this.request);

			List<User> matchingUsers = new ArrayList<User>();
			User testUserWorkbench = new User();
			testUserWorkbench.setName(BMSPreAuthenticatedUsersRolePopulatorTest.TEST_USER);
			testUserWorkbench.setPassword("password");
			UserRole testUserRole = new UserRole(testUserWorkbench, "ADMIN");
			testUserWorkbench.setRoles(Arrays.asList(testUserRole));
			matchingUsers.add(testUserWorkbench);

			Mockito.when(
					this.workbenchDataManager.getUserByName(BMSPreAuthenticatedUsersRolePopulatorTest.TEST_USER, 0, 1, Operation.EQUAL))
					.thenReturn(matchingUsers);

			PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails roleDetails =
					(PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails) this.rolesPopulator.buildDetails(this.request);
			Assert.assertEquals(testUserWorkbench.getRoles().size(), roleDetails.getGrantedAuthorities().size());
			Assert.assertEquals(SecurityUtil.ROLE_PREFIX + testUserRole.getRole(), roleDetails.getGrantedAuthorities().get(0)
					.getAuthority());

		} catch (MiddlewareQueryException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test(expected = AuthenticationServiceException.class)
	public void testLoadUserDataAccessError() throws MiddlewareQueryException {
		Mockito.when(this.request.getParameter(ContextConstants.PARAM_AUTH_TOKEN)).thenReturn(
				BMSPreAuthenticatedUsersRolePopulatorTest.AUTH_TOKEN);
		Mockito.when(this.workbenchDataManager.getUserByName(BMSPreAuthenticatedUsersRolePopulatorTest.TEST_USER, 0, 1, Operation.EQUAL))
				.thenThrow(new MiddlewareQueryException("Boom!"));
		this.rolesPopulator.buildDetails(this.request);
	}

}
