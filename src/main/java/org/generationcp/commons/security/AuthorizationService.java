package org.generationcp.commons.security;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

	@Autowired
	private UserService userService;

	public AuthorizationService() {
	}

	public Boolean isSuperAdminUser() {
		final WorkbenchUser workbenchUser = this.userService.getUserByUsername(SecurityUtil.getLoggedInUserName());
		if (workbenchUser != null) {
			return workbenchUser.isSuperAdmin();
		}
		return false;
	}

}
