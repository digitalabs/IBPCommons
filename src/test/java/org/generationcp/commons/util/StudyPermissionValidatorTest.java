package org.generationcp.commons.util;

import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class StudyPermissionValidatorTest {

	private static final int USER_ID = 101;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private AuthorizationService authorizationService;

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
		Mockito.doReturn(USER_ID).when(this.contextUtil).getCurrentWorkbenchUserId();
		Assert.assertFalse(this.validator.userLacksPermissionForStudy(study));
	}

	@Test
	public void testUserLacksPermissionForStudyWhenStudyIsLockedButUserIsSuperAdmin() {
		final StudyReference study = createTestStudy(true);
		Mockito.doReturn(USER_ID + 1).when(this.contextUtil).getCurrentWorkbenchUserId();

		Mockito.when(authorizationService.isSuperAdminUser()).thenReturn(Boolean.TRUE);

		Assert.assertFalse(this.validator.userLacksPermissionForStudy(study));
	}

	@Test
	public void testUserLacksPermissionForStudyWhenStudyIsLockedAndUserNotOwnerNorSuperAdmin() {
		final StudyReference study = createTestStudy(true);
		Mockito.doReturn(USER_ID + 1).when(this.contextUtil).getCurrentWorkbenchUserId();

		Assert.assertTrue(this.validator.userLacksPermissionForStudy(study));
	}

	private StudyReference createTestStudy(boolean isLocked) {
		final StudyReference study = new StudyReference(1, "Some Study");
		study.setIsLocked(isLocked);
		study.setOwnerId(USER_ID);
		return study;
	}

}
