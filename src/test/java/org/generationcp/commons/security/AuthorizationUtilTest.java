package org.generationcp.commons.security;

import com.google.common.collect.Lists;
import org.generationcp.middleware.pojos.workbench.PermissionsEnum;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import junit.framework.Assert;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationUtilTest {

	private static final String PASSWORD = "admin2";
	private static final String USERNAME = "admin1";

	@Mock
	private UserService userService;

	@InjectMocks
	private AuthorizationUtil authorizationUtil;

	@Before
	public void setup() {
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + PermissionsEnum.ADMIN.name());
		final UsernamePasswordAuthenticationToken loggedInUser =
			new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(loggedInUser);
	}

	@Test
	public void testIsSuperAdminUser_True() {
		final List<UserRole> userRoleList = new ArrayList<>();
		final UserRole userRole = new UserRole();
		userRole.setRole(new Role("Super admin role", Role.SUPERADMIN));
		userRoleList.add(userRole);
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setRoles(userRoleList);

		Mockito.when(userService.getUserByUsername(USERNAME)).thenReturn(workbenchUser);

		Assert.assertTrue(authorizationUtil.isSuperAdminUser());
	}
	
	@Test
	public void testIsSuperAdminUser_False() {
		final List<UserRole> userRoleList = new ArrayList<>();
		final UserRole userRole = new UserRole();
		userRole.setRole(new Role("admin role", "ADMIN"));
		userRoleList.add(userRole);
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setRoles(userRoleList);

		Mockito.when(userService.getUserByUsername(USERNAME)).thenReturn(workbenchUser);

		Assert.assertFalse(authorizationUtil.isSuperAdminUser());
	}
}
