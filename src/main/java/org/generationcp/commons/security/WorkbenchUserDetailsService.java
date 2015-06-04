
package org.generationcp.commons.security;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WorkbenchUserDetailsService implements UserDetailsService {

	private WorkbenchDataManager workbenchDataManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			List<User> matchingUsers = this.workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL);
			if (matchingUsers != null && !matchingUsers.isEmpty()) {
				User workbenchUser = matchingUsers.get(0);
				// FIXME Populate flags for accountNonExpired, credentialsNonExpired, accountNonLocked properly, all true for now.
				return new org.springframework.security.core.userdetails.User(workbenchUser.getName(), workbenchUser.getPassword(),
						SecurityUtil.getRolesAsAuthorities(workbenchUser));
			}
			throw new UsernameNotFoundException("Invalid username/password.");
		} catch (MiddlewareQueryException e) {
			throw new AuthenticationServiceException("Data access error while authenticaing user against Workbench.", e);
		}
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
