
package org.generationcp.commons.security;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.permission.PermissionService;
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
import java.util.Collection;

public class BMSPreAuthenticatedUsersRolePopulator implements AuthenticationDetailsSource<HttpServletRequest, GrantedAuthoritiesContainer> {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private PermissionService permissionService;

	@Override
	public GrantedAuthoritiesContainer buildDetails(final HttpServletRequest request) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

		return transactionTemplate.execute(new TransactionCallback<PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails>() {

			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails doInTransaction(TransactionStatus status) {
				try {
					final WorkbenchUser user = ContextUtil.getCurrentWorkbenchUser(BMSPreAuthenticatedUsersRolePopulator.this.workbenchDataManager, request);
					final Project project = ContextUtil.getProjectInContext(BMSPreAuthenticatedUsersRolePopulator.this.workbenchDataManager, request);

					final Collection<? extends GrantedAuthority> authorities =
						SecurityUtil.getAuthorities(BMSPreAuthenticatedUsersRolePopulator.this.permissionService.getPermissions( //
							user.getUserid(), //
							project.getCropType().getCropName(), //
							project.getProjectId().intValue()));

					return new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(request, authorities);
				} catch (final MiddlewareQueryException e) {
					throw new AuthenticationServiceException("Data access error while resolving Workbench user roles.", e);
				}
			}

		});

	}

	void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
