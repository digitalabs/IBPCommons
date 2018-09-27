package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fest.util.Collections;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class AuthorizationUtil {
	
	protected static final String ACCESS_DENIED_MESSAGE = "Access Denied. User does not have appropriate role to access the functionality.";

	public static void preAuthorize(final List<Role> configuredRoles) {
		if (Collections.isNullOrEmpty(configuredRoles)) {
			throw new AccessDeniedException("Access Denied. No role is configured to access this functionality.");
		}
		final List<String> permittedRoleNames = new ArrayList<>();
		for (final Role role : configuredRoles) {
			permittedRoleNames.add(role.getDescription());
		}
		AuthorizationUtil.preAuthorizeUser(permittedRoleNames);
		
	}
	
	private static void preAuthorizeUser(final List<String> roleNames){
		final Collection<? extends GrantedAuthority> authorities = SecurityUtil.getLoggedInUserRoles();
		final List<String> capitalizedRoles = Lists.transform(roleNames, new Function<String, String>() {
			@Override
			public String apply(final String role) {
				return role.toUpperCase();
			}
		});
		
		for (final GrantedAuthority grantedAuthority : authorities) {
			final String authority = grantedAuthority.getAuthority();
			final String roleName = authority.split(SecurityUtil.ROLE_PREFIX)[1].toUpperCase();
			if (!capitalizedRoles.contains(roleName)) {
				throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
			}

		}
	}
	
	public static void preAuthorizeAdminAuthority() {
		preAuthorizeUser(Arrays.asList(Role.SUPERADMIN, Role.ADMIN));
	}
	
	public static Boolean isSuperAdminUser() {
		try {
			preAuthorizeUser(Arrays.asList(Role.SUPERADMIN));
			return true;
		} catch (final AccessDeniedException exception){
			// we are just checking if user has SUPERADMIN role, no need to re-throw or log exception
		}
		return false;
	}
	
	
	
	
}
