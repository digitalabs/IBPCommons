
package org.generationcp.commons.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.service.UserTreeStateService;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.UserProgramTreeState;

public class UserTreeStateServiceImpl implements UserTreeStateService {

	public static final String GERMPLASM_LIST_ROOT_ITEM = "LISTS";
    @Resource
	private UserProgramStateDataManager userProgramStateDataManager;

	@Resource
	private GermplasmListManager germplasmListManager;

	@Override
	public List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(int userId, String programUuid, String type) {

		return userProgramStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, type);

	}

	@Override
	public List<String> getUserProgramTreeStateForSaveList(int userId, String programUuid) {
		GermplasmList germplasmList = this.germplasmListManager.getLastSavedGermplasmListByUserId(userId, programUuid);
        List<String> treeState;

		// if no lists have been saved yet, attempt to retrieve the user's tree navigation state
		if (germplasmList == null) {
            treeState = this.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, ListTreeState.GERMPLASM_LIST.name());
            treeState.add(0, USE_PREVIOUS_NAVIGATION_MARKER);
		} else {
            treeState = this.computeTreeStateForSavedTree(germplasmList);
            treeState.add(0, USE_LAST_SAVED_MARKER);
		}

        return treeState;
	}

	@Override
	public UserProgramTreeState saveOrUpdateUserProgramTreeState(int userId, String programUuid, String type, List<String> treeState) {
		return userProgramStateDataManager.saveOrUpdateUserProgramTreeState(userId, programUuid, type, treeState);
	}

	/**
	 * Generates a list consisting of the list IDs representing the folders starting from the root leading up to the provided list
	 *
	 * @param germplasmList
	 * @return
	 */
	protected List<String> computeTreeStateForSavedTree(final GermplasmList germplasmList) {
		List<String> treeState = new ArrayList<>();

		GermplasmList current = germplasmList.isList() ? germplasmList.getParent() : germplasmList;

		while (current != null && current.getId() != 0) {
			treeState.add(0, current.getId().toString());

			current = current.getParent();
		}

		treeState.add(0, GERMPLASM_LIST_ROOT_ITEM);

		return treeState;
	}
}
