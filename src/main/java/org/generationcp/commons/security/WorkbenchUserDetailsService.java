
package org.generationcp.commons.security;

import org.apache.commons.lang.StringEscapeUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class WorkbenchUserDetailsService implements UserDetailsService {

	private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			// username must be converted from html-encode to utf-8 string to support chinese/utf-8 languages
			username = StringEscapeUtils.unescapeHtml(username);

			final List<WorkbenchUser> matchingUsers = this.userService.getUserByName(username, 0, 1, Operation.EQUAL);
			if (matchingUsers != null && !matchingUsers.isEmpty()) {
				final WorkbenchUser workbenchUser = matchingUsers.get(0);
				// FIXME Populate flags for accountNonExpired, credentialsNonExpired, accountNonLocked properly, all true for now.
				return new org.springframework.security.core.userdetails.User(workbenchUser.getName(), workbenchUser.getPassword(),
						SecurityUtil.getRolesAsAuthorities(workbenchUser));
			}
			throw new UsernameNotFoundException("Invalid username/password.");
		} catch (final MiddlewareQueryException e) {
			throw new AuthenticationServiceException("Data access error while authenticaing user against Workbench.", e);
		}
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}
}
