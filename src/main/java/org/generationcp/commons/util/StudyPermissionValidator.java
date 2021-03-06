
package org.generationcp.commons.util;

import javax.annotation.Resource;

import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class StudyPermissionValidator {

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private StudyDataManager studyDataManager;

	@Autowired
	private AuthorizationService authorizationService;

	public Boolean userLacksPermissionForStudy(final Integer studyId) {
		final StudyReference study = this.studyDataManager.getStudyReference(studyId);
		return userLacksPermissionForStudy(study);
	}

	public Boolean userLacksPermissionForStudy(final StudyReference study) {
		if (study != null) {
			return userLacksPermissionForStudy(study.getIsLocked(), study.getOwnerId(),
					this.contextUtil.getCurrentWorkbenchUserId());
		}
		return false;
	}

	private Boolean userLacksPermissionForStudy(final Boolean isLocked, final Integer ownerId, final Integer currentUserId) {
		return isLocked && !authorizationService.isSuperAdminUser() && !currentUserId.equals(ownerId);
	}
}
