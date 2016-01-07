
package org.generationcp.commons.service;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserProgramTreeState;

public interface UserTreeStateService {

	List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(int userId, String programUuid, String type)
			throws MiddlewareQueryException;

	List<String> getUserProgramTreeStateForSaveList(int userId, String programUuid);

	UserProgramTreeState saveOrUpdateUserProgramTreeState(int userId, String programUuid, String type, List<String> treeState)
			throws MiddlewareQueryException;
}
