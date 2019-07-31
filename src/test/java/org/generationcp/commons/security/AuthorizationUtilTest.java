package org.generationcp.commons.security;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
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
		Mockito.when(securityUtil.getLoggedInUserRoles()).thenReturn(userRoleList);
		Assert.assertTrue(authorizationUtil.isSuperAdminUser());
	}
	
	@Test
	public void testIsSuperAdminUserWithAdminRoleLoggedIn() {
		final List<UserRole> userRoleList = new ArrayList<>();
		final UserRole userRole = new UserRole();
		userRole.setRole(new Role("admin role", "ADMIN"));
		userRoleList.add(userRole);
		Mockito.when(securityUtil.getLoggedInUserRoles()).thenReturn(userRoleList);
		Assert.assertFalse(authorizationUtil.isSuperAdminUser());
	}
}
