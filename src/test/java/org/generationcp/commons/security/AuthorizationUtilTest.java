package org.generationcp.commons.security;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationUtilTest {

	@Mock
	private SecurityUtil securityUtil;

	@InjectMocks
	private AuthorizationUtil authorizationUtil;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testIsSuperAdminUserWithSuperAdminRoleLoggedIn() {
		final List<UserRole> userRoleList = new ArrayList<>();
		final UserRole userRole = new UserRole();
		userRole.setRole(new Role("Super admin role", Role.SUPERADMIN));
		userRoleList.add(userRole);
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setRoles(userRoleList);
		Mockito.when(securityUtil.getLoggedInUser()).thenReturn(workbenchUser);
		Assert.assertTrue(authorizationUtil.isSuperAdminUser());
	}
	
	@Test
	public void testIsSuperAdminUserWithAdminRoleLoggedIn() {
		final List<UserRole> userRoleList = new ArrayList<>();
		final UserRole userRole = new UserRole();
		userRole.setRole(new Role("admin role", "ADMIN"));
		userRoleList.add(userRole);
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setRoles(userRoleList);
		Mockito.when(securityUtil.getLoggedInUser()).thenReturn(workbenchUser);
		Assert.assertFalse(authorizationUtil.isSuperAdminUser());
	}
}
