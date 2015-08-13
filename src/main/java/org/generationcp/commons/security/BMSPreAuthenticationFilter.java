
package org.generationcp.commons.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class BMSPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(BMSPreAuthenticationFilter.class);
	
	@Resource
	WorkbenchDataManager workbenchDataManager;

	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Override
	protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

		return transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				try {
					return ContextUtil.getCurrentWorkbenchUsername(BMSPreAuthenticationFilter.this.workbenchDataManager, request);
				} catch (MiddlewareQueryException e) {
					BMSPreAuthenticationFilter.LOG.error(e.getMessage(), e);
				}
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "";
	}

}
