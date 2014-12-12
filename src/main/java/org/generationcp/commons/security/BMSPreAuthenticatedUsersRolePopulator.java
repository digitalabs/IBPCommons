package org.generationcp.commons.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class BMSPreAuthenticatedUsersRolePopulator
		implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Override
	public GrantedAuthoritiesContainer buildDetails(HttpServletRequest request) {
		try {
			User user = ContextUtil.getCurrentWorkbenchUser(workbenchDataManager, request);

			List<GrantedAuthority> role = new ArrayList<>();
			role.addAll(SecurityUtil.getRolesAsAuthorities(user));

			return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, role);

		} catch (MiddlewareQueryException e) {

			throw new AuthenticationServiceException(
					"Data access error while resolving Workbench user roles.", e);
		}
	}

	void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
