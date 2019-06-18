
package org.generationcp.commons.security;

import org.apache.commons.lang.StringEscapeUtils;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public class WorkbenchUserDetailsService implements UserDetailsService {

	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private HttpServletRequest request;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			// username must be converted from html-encode to utf-8 string to support chinese/utf-8 languages
			username = StringEscapeUtils.unescapeHtml(username);

			List<WorkbenchUser> matchingUsers = this.workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL);
			if (matchingUsers != null && !matchingUsers.isEmpty()) {
				WorkbenchUser workbenchUser = matchingUsers.get(0);

				final Project project = ContextUtil.getProjectInContext(this.workbenchDataManager, request);

				final Collection<? extends GrantedAuthority> authorities =
					SecurityUtil.getAuthorities(this.permissionService.getPermissions( //
						workbenchUser.getUserid(), //
						project.getCropType().getCropName(), //
						project.getProjectId().intValue()));

				// FIXME Populate flags for accountNonExpired, credentialsNonExpired, accountNonLocked properly, all true for now.
				return new User(workbenchUser.getName(), workbenchUser.getPassword(), authorities);
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
