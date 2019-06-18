
package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtilTest {

	private static final String TEST_USER = "testUser";
	private static final String PERMISSION_NAME = "ADMIN";

	@Test
	public void testGetRolesAsAuthorities() {
		WorkbenchUser testUserWorkbench = new WorkbenchUser();
		testUserWorkbench.setName(SecurityUtilTest.TEST_USER);
		final List<PermissionDto> permissions = new ArrayList<>();
		final PermissionDto permission = new PermissionDto();
		permission.setName(PERMISSION_NAME);
		permissions.add(permission);
		testUserWorkbench.setPermissions(permissions);

		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getAuthorities(testUserWorkbench.getPermissions());
		Assert.assertEquals(1, rolesAsAuthorities.size());
		Assert.assertTrue(rolesAsAuthorities.contains(new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + PERMISSION_NAME)));
	}

	@Test
	public void testGetRolesAsAuthorities2() {
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getAuthorities(null);
		Assert.assertTrue("Expecting an empty collection when input user is null.", rolesAsAuthorities.isEmpty());
	}

	@Test
	public void testGetRolesAsAuthorities3() {
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getAuthorities(new ArrayList<PermissionDto>());
		Assert.assertTrue("Expecting an empty collection when user roles are null.", rolesAsAuthorities.isEmpty());
	}

	@Test
	public void testGetRolesAsAuthorities4() {
		WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setPermissions(Collections.<PermissionDto>emptyList());
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getAuthorities(new ArrayList<PermissionDto>());
		Assert.assertTrue("Expecting an empty collection when user roles are empty.", rolesAsAuthorities.isEmpty());
	}

}
