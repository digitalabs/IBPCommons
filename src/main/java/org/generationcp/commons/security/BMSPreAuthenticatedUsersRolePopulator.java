
package org.generationcp.commons.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class BMSPreAuthenticatedUsersRolePopulator implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

	@Autowired
	private UserService userService;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	public GrantedAuthoritiesContainer buildDetails(final HttpServletRequest request) {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

		return transactionTemplate.execute(new TransactionCallback<PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails>() {

			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails doInTransaction(final TransactionStatus status) {
				try {
					final WorkbenchUser user = ContextUtil.getCurrentWorkbenchUser(BMSPreAuthenticatedUsersRolePopulator.this.userService, request);

					final List<GrantedAuthority> role = new ArrayList<>();
					role.addAll(SecurityUtil.getRolesAsAuthorities(user));

					return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, role);

				} catch (final MiddlewareQueryException e) {

					throw new AuthenticationServiceException("Data access error while resolving Workbench user roles.", e);
				}
			}

		});

	}

	void setUserService(final UserService userService) {
		this.userService = userService;
	}

	void setTransactionManager(final PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
