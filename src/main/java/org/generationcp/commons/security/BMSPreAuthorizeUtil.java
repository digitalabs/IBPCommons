package org.generationcp.commons.security;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.util.StringUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class BMSPreAuthorizeUtil {

	public static void preAuthorize(final String configuredRoles) {

		final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		if (StringUtils.isEmpty(configuredRoles)) {
			throw new AccessDeniedException("No role is configured to access this link");
		}

		final List<String> permittedRoleList = Lists.newArrayList(configuredRoles.split(","));

		for (final GrantedAuthority grantedAuthority : authorities) {
			final String authority = grantedAuthority.getAuthority();
			if (!StringUtil.containsIgnoreCase(permittedRoleList, authority.split(SecurityUtil.ROLE_PREFIX)[1])) {
				throw new AccessDeniedException("You have not authorized role to access this link");
			}

		}
	}
}
