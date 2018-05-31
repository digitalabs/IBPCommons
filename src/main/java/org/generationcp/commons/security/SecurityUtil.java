
package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

	/**
	 * While role prefix is configurable and can be set to empty, having a prefix is recommended. See JavaDoc for
	 * org.springframework.security.access.vote.RoleVoter
	 */
	public static final String ROLE_PREFIX = "ROLE_";

	private SecurityUtil() {
	}

	public static String getLoggedInUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		return null;
	}

	public static Collection<? extends GrantedAuthority> getLoggedInUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication.getAuthorities();
		}
		return Collections.emptyList();
	}

	public static Collection<? extends GrantedAuthority> getRolesAsAuthorities(WorkbenchUser workbenchUser) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (workbenchUser != null) {
			List<UserRole> userRoles = workbenchUser.getRoles();
			if (userRoles != null && !userRoles.isEmpty()) {
				for (UserRole role : userRoles) {
					authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + role.getRole().getCapitalizedName()));
				}
			}
		}
		return authorities;
	}

	public static String getEncodedToken() {
		return Base64.encodeBase64URLSafeString(SecurityUtil.getLoggedInUserName().getBytes());
	}

	public static String decodeToken(String token) {
		return new String(Base64.decodeBase64(token));
	}
}
