
package org.generationcp.commons.service;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserProgramTreeState;

public interface UserTreeStateService {

    String USE_LAST_SAVED_MARKER = "SAVED";
    String USE_PREVIOUS_NAVIGATION_MARKER = "NAVIGATION";

    List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(int userId, String programUuid, String type)
			throws MiddlewareQueryException;

	List<String> getUserProgramTreeStateForSaveList(int userId, String programUuid);

	List<String> getUserProgramTreeStateForSaveSampleList(int userId, String programUuid, String type);


	UserProgramTreeState saveOrUpdateUserProgramTreeState(int userId, String programUuid, String type, List<String> treeState)
			throws MiddlewareQueryException;
}
