package org.generationcp.commons.security;

import java.util.Collections;

import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;
import junit.framework.Assert;

public class AuthorizationUtilTest {

	private UsernamePasswordAuthenticationToken loggedInUser;

	@Test
	public void testPreAuthorizeSuccessWithAdminConfiguredAndAdminUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(Collections.singletonList(new Role(1, "Admin")));
		} catch (AccessDeniedException e) {
			Assert.fail("Access Import germplsm link should not throw Access Denied exception.");
		}
	}

	@Test
	public void testPreAuthorizeFailWithAdminConfiguredAndTechnicianUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "TECHNICIAN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(Collections.singletonList(new Role(1, "Admin")));
		} catch (AccessDeniedException e) {
			Assert.assertEquals("Access Denied. User does not have appropriate role to access the functionality.", e.getMessage());
		}
	}

	@Test
	public void testPreAuthorizefailWithNoRoleConfiguredAndTechnicianUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "TECHNICIAN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(null);
		} catch (AccessDeniedException e) {
			Assert.assertEquals("Access Denied. No role is configured to access this functionality.", e.getMessage());
		}
	}

}
