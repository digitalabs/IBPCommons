
package org.generationcp.commons.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtilTest {

	private static final String TEST_USER = "testUser";

	@Test
	public void testGetRolesAsAuthorities() {
		WorkbenchUser testUserWorkbench = new WorkbenchUser();
		testUserWorkbench.setName(SecurityUtilTest.TEST_USER);
		UserRole testUserRole = new UserRole(testUserWorkbench, new Role(1, "Admin"));
		testUserWorkbench.setRoles(Arrays.asList(testUserRole));

		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getRolesAsAuthorities(testUserWorkbench);
		Assert.assertEquals(1, rolesAsAuthorities.size());
		Assert.assertTrue(rolesAsAuthorities.contains(new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + testUserRole.getRole().getCapitalizedRole())));
	}

	@Test
	public void testGetRolesAsAuthorities2() {
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getRolesAsAuthorities(null);
		Assert.assertTrue("Expecting an empty collection when input user is null.", rolesAsAuthorities.isEmpty());
	}

	@Test
	public void testGetRolesAsAuthorities3() {
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getRolesAsAuthorities(new WorkbenchUser());
		Assert.assertTrue("Expecting an empty collection when user roles are null.", rolesAsAuthorities.isEmpty());
	}

	@Test
	public void testGetRolesAsAuthorities4() {
		WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setRoles(Collections.<UserRole>emptyList());
		Collection<? extends GrantedAuthority> rolesAsAuthorities = SecurityUtil.getRolesAsAuthorities(workbenchUser);
		Assert.assertTrue("Expecting an empty collection when user roles are empty.", rolesAsAuthorities.isEmpty());
	}

}
