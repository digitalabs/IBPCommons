package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;


public class BMSPreAuthenticatedUsersRolePopulator implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	
	@Override
	public GrantedAuthoritiesContainer buildDetails(HttpServletRequest request) {
		
		String ssUserName = request.getParameter(ContextConstants.PARAM_AUTH_TOKEN);
		try {
			List<User> matchingUsers = workbenchDataManager.getUserByName(ssUserName, 0, 1, Operation.EQUAL);
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
			if(matchingUsers != null && !matchingUsers.isEmpty()) {
				User workbenchUser = matchingUsers.get(0);
				roles.addAll(SecurityUtil.getRolesAsAuthorities(workbenchUser));
			}
			return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, roles);
		} catch (MiddlewareQueryException e) {
			throw new AuthenticationServiceException("Data access error while resolving Workbench user roles.", e);
		}
	}

	void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
