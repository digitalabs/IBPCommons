package org.generationcp.commons.security;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WorkbenchUserDetailsService implements UserDetailsService {
	
	static final Logger LOGGER = LoggerFactory.getLogger(WorkbenchUserDetailsService.class);
	
	private WorkbenchDataManager workbenchDataManager;
	
	@Autowired
	public WorkbenchUserDetailsService(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			List<User> workbenchUser = workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL);
			if(workbenchUser != null && !workbenchUser.isEmpty()) {
				User user = workbenchUser.get(0);
				//FIXME load authorities based on Worbench roles, hard coded for now.
				List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
				roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				roles.add(new SimpleGrantedAuthority("ROLE_BREEDER"));
				roles.add(new SimpleGrantedAuthority("ROLE_TECHNICIAN"));
				//TODO Populate flags for accountNonExpired, credentialsNonExpired, accountNonLocked properly, all true for now.
				return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), roles);
			}
			throw new UsernameNotFoundException("Invalid username/password.");
		} catch (MiddlewareQueryException e) {
			throw new AuthenticationServiceException("Data access error while authenticaing user against Workbench.", e);
		}
	}
} 
