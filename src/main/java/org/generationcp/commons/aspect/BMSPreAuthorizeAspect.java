package org.generationcp.commons.aspect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.generationcp.middleware.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

@Aspect
//@Component
public class BMSPreAuthorizeAspect {

	@Autowired
	Environment environment;

	@Before("@annotation(org.generationcp.commons.aspect.BMSPreAuthorize)")
	public void beforeBMSPreAuthorize(JoinPoint joinPoint) {
		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		final Method method = signature.getMethod();

		final BMSPreAuthorize myAnnotation = method.getAnnotation(BMSPreAuthorize.class);
		final String configuredProperty = myAnnotation.configuredProperty();

		final String propertyValues = environment.getProperty(configuredProperty);

		final List<String> configuredRoles = Lists.newArrayList(propertyValues.split(","));

		final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		for (GrantedAuthority grantedAuthority : authorities) {
			if (!StringUtil.containsIgnoreCase(configuredRoles, grantedAuthority.getAuthority())) {
				throw new AccessDeniedException("You have not authorized role to access this link");
			}
		}

	}
}
