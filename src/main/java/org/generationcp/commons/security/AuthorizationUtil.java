package org.generationcp.commons.security;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AuthorizationUtil {
	
	protected static final String ACCESS_DENIED_MESSAGE = "Access Denied. User does not have appropriate role to access the functionality.";

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
		preAuthorizeUser(Arrays.asList(Role.SUPERADMIN));
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
