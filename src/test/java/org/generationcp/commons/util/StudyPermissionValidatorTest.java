package org.generationcp.commons.util;

import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class StudyPermissionValidatorTest {

	private static final int USER_ID = 101;

	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@InjectMocks
	private StudyPermissionValidator validator;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testUserLacksPermissionForStudyWhenStudyDoesntExist() {
		Mockito.doReturn(null).when(this.studyDataManager).getStudyReference(1);
		Assert.assertFalse(this.validator.userLacksPermissionForStudy(1));
	}
	
	@Test
	public void testUserLacksPermissionForStudyWhenStudyNotLocked() {
		final StudyReference study = createTestStudy(false);
		Assert.assertFalse(this.validator.userLacksPermissionForStudy(study));
	}
	
	@Test
	public void testUserLacksPermissionForStudyWhenStudyIsLockedButUserIsOwner() {
		final StudyReference study = createTestStudy(true);
		Mockito.doReturn(USER_ID).when(this.contextUtil).getCurrentIbdbUserId();
		Assert.assertFalse(this.validator.userLacksPermissionForStudy(study));
	}
	
	@Test
	public void testUserLacksPermissionForStudyWhenStudyIsLockedButUserIsSuperAdmin() {
		final StudyReference study = createTestStudy(true);
		Mockito.doReturn(USER_ID + 1).when(this.contextUtil).getCurrentIbdbUserId();
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.SUPERADMIN);
		UsernamePasswordAuthenticationToken loggedInUser = new UsernamePasswordAuthenticationToken("", "", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(loggedInUser);
		
		Assert.assertFalse(this.validator.userLacksPermissionForStudy(study));
	}
	
	@Test
	public void testUserLacksPermissionForStudyWhenStudyIsLockedAndUserNotOwnerNorSuperAdmin() {
		final StudyReference study = createTestStudy(true);
		Mockito.doReturn(USER_ID + 1).when(this.contextUtil).getCurrentIbdbUserId();
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + Role.ADMIN);
		UsernamePasswordAuthenticationToken loggedInUser = new UsernamePasswordAuthenticationToken("", "", Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(loggedInUser);
		
		Assert.assertTrue(this.validator.userLacksPermissionForStudy(study));
	}

	private StudyReference createTestStudy(boolean isLocked) {
		final StudyReference study = new StudyReference(1, "Some Study");
		study.setIsLocked(isLocked);
		study.setOwnerId(USER_ID);
		return study;
	}

}
