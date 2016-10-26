package org.generationcp.commons.security;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.util.StringUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class AuthorizationUtil {

	public static void preAuthorize(final String configuredRoles) {

		final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		if (StringUtils.isEmpty(configuredRoles)) {
			throw new AccessDeniedException("Access Denied. No role is configured to access this functionality.");
		}

		final List<String> permittedRoleList = Lists.newArrayList(configuredRoles.split(","));

		for (final GrantedAuthority grantedAuthority : authorities) {
			final String authority = grantedAuthority.getAuthority();
			if (!StringUtil.containsIgnoreCase(permittedRoleList, authority.split(SecurityUtil.ROLE_PREFIX)[1])) {
				throw new AccessDeniedException("Access Denied. User does not have appropriate role to access the functionality.");
			}

		}
	}
}
