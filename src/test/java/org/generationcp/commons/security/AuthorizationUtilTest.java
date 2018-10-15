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

	private static final String PASSWORD = "admin2";
	private static final String USERNAME = "admin1";
	private UsernamePasswordAuthenticationToken loggedInUser;

	@Test
	public void testPreAuthorizeSuccessWithAdminConfiguredAndAdminUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(Collections.singletonList(new Role(1, "Admin")));
		} catch (AccessDeniedException e) {
			Assert.fail("Should not throw Access Denied exception.");
		}
	}

	@Test
	public void testPreAuthorizeFailWithAdminConfiguredAndTechnicianUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "TECHNICIAN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(Collections.singletonList(new Role(1, "Admin")));
			Assert.fail("Expecting to throws AccessDeniedException but did not.");
		} catch (AccessDeniedException e) {
			Assert.assertEquals("Access Denied. User does not have appropriate role to access the functionality.", e.getMessage());
		}
	}

	@Test
	public void testPreAuthorizefailWithNoRoleConfiguredAndTechnicianUserLoggedIn() throws Exception {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "TECHNICIAN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorize(null);
			Assert.fail("Expecting to throws AccessDeniedException but did not.");
		} catch (AccessDeniedException e) {
			Assert.assertEquals("Access Denied. No role is configured to access this functionality.", e.getMessage());
		}
	}
	
	@Test
	public void testPreAuthorizeAdminAuthorityWithAdminRoleLoggedIn() {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.ADMIN);

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorizeAdminAuthority();
		} catch (AccessDeniedException e) {
			Assert.fail("Expecting not to throw AccessDeniedException but was thrown.");
		}
	}

	@Test
	public void testPreAuthorizeAdminAuthorityWithSuperAdminRoleLoggedIn() {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.SUPERADMIN);

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorizeAdminAuthority();
		} catch (AccessDeniedException e) {
			Assert.fail("Expecting not to throw AccessDeniedException but was thrown.");
		}
	}
	
	@Test
	public void testPreAuthorizeAdminAuthorityWithTechnicianRoleLoggedIn() {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "TECHNICIAN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			AuthorizationUtil.preAuthorizeAdminAuthority();
			Assert.fail("Expecting to throw AccessDeniedException but did not.");
		} catch (AccessDeniedException e) {
			Assert.assertEquals("Access Denied. User does not have appropriate role to access the functionality.", e.getMessage());
		}
	}
	
	@Test
	public void testIsSuperAdminUserWithSuperAdminRoleLoggedIn() {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.SUPERADMIN);

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			Assert.assertTrue(AuthorizationUtil.isSuperAdminUser());
		} catch (AccessDeniedException e) {
			Assert.fail("Expecting not to throw AccessDeniedException but was thrown.");
		}
	}
	
	@Test
	public void testIsSuperAdminUserWithAdminRoleLoggedIn() {
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.ADMIN);

		this.loggedInUser = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(this.loggedInUser);
		try {
			Assert.assertFalse(AuthorizationUtil.isSuperAdminUser());
		} catch (AccessDeniedException e) {
			Assert.fail("Expecting not to throw AccessDeniedException but was thrown.");
		}
	}
}
