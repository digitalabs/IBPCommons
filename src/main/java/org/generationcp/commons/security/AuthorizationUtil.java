package org.generationcp.commons.security;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationUtil {

	@Autowired
	private SecurityUtil securityUtil;

	public AuthorizationUtil() {
	}

	public Boolean isSuperAdminUser() {
		final List<UserRole> userRoles = securityUtil.getLoggedInUserRoles();
		boolean found = false;
		for (final UserRole userRole : userRoles) {
			if (userRole.getRole().getName().toUpperCase().equals(Role.SUPERADMIN)) {
				found = true;
				break;
			}
		}
		return found;
	}

}
