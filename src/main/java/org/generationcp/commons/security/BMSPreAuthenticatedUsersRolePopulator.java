package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;


public class BMSPreAuthenticatedUsersRolePopulator implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

	@Override
	public GrantedAuthoritiesContainer buildDetails(HttpServletRequest request) {
		//FIXME : need to query the authentication source and retrieve the roles!
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		roles.add(new SimpleGrantedAuthority("ROLE_BREEDER"));
		roles.add(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));
		return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, roles);
	}

}
