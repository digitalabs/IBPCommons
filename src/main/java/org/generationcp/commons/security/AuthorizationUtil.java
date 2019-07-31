package org.generationcp.commons.security;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AuthorizationUtil {

	@Autowired
	private SecurityUtil securityUtil;

	public AuthorizationUtil() {
	}

	public Boolean isSuperAdminUser() {
		final WorkbenchUser workbenchUser = securityUtil.getLoggedInUser();
		if (workbenchUser != null) {
			return workbenchUser.isSuperAdmin();
		}
		return false;
	}

}
