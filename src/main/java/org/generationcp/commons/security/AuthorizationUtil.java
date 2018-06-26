package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fest.util.Collections;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.util.StringUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;

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
		for (final GrantedAuthority grantedAuthority : authorities) {
			final String authority = grantedAuthority.getAuthority();
			if (!StringUtil.containsIgnoreCase(roleNames, authority.split(SecurityUtil.ROLE_PREFIX)[1])) {
				throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
			}

		}
	}
	
	public static void preAuthorizeAdminAuthority() {
		preAuthorizeUser(Arrays.asList(Role.SUPERADMIN, Role.ADMIN));
	}
	
	
	
	
}
