package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {
	
	public static final String ROLE_PREFIX = "ROLE_";

	public static String getLoggedInUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			return authentication.getName();
		}
		return null;
	}
	
	public static Collection<? extends GrantedAuthority> getLoggedInUserRoles() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			return authentication.getAuthorities();
		}
		return Collections.emptyList();
	}
	
	public static Collection<? extends GrantedAuthority> getRolesAsAuthorities(User workbenchUser) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (workbenchUser != null) {
			List<UserRole> userRoles = workbenchUser.getRoles();
			if (userRoles != null && !userRoles.isEmpty()) {
				for (UserRole role : userRoles) {
					authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getRole()));
				}
			}
		}
		return authorities;
	}
}
