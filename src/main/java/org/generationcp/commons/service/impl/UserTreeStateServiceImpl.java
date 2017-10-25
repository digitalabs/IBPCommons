
package org.generationcp.commons.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.service.UserTreeStateService;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.UserProgramTreeState;
import org.generationcp.middleware.service.api.SampleListService;

public class UserTreeStateServiceImpl implements UserTreeStateService {

	public static final String GERMPLASM_LIST_ROOT_ITEM = "LISTS";
    @Resource
	private UserProgramStateDataManager userProgramStateDataManager;

	@Resource
	private GermplasmListManager germplasmListManager;

	@Resource
	private SampleListService sampleListService;

	@Override
	public List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(final int userId, final String programUuid, final String type) {
		return userProgramStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, type);
	}

	@Override
	public List<String> getUserProgramTreeStateForSaveList(final int userId, final String programUuid) {
		final GermplasmList germplasmList = this.germplasmListManager.getLastSavedGermplasmListByUserId(userId, programUuid);
        final List<String> treeState;

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
	public List<String> getUserProgramTreeStateForSaveSampleList(final int userId, final String programUuid, String type) {
		final SampleList sampleList = this.sampleListService.getLastSavedSampleListByUserId(userId, programUuid);
		final List<String> treeState;

		// if no lists have been saved yet, attempt to retrieve the user's tree navigation state
		if (sampleList == null) {
			treeState = this.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, type);
			treeState.add(0, USE_PREVIOUS_NAVIGATION_MARKER);
		} else {
			treeState = this.computeTreeStateForSavedSampleListTree(sampleList);
			treeState.add(0, USE_LAST_SAVED_MARKER);
		}

		return treeState;
	}

	@Override
	public UserProgramTreeState saveOrUpdateUserProgramTreeState(final int userId, final String programUuid, final String type, final List<String> treeState) {
		return userProgramStateDataManager.saveOrUpdateUserProgramTreeState(userId, programUuid, type, treeState);
	}

	/**
	 * Generates a list consisting of the list IDs representing the folders starting from the root leading up to the provided list
	 *
	 * @param germplasmList
	 * @return
	 */
	protected List<String> computeTreeStateForSavedTree(final GermplasmList germplasmList) {
		final List<String> treeState = new ArrayList<>();

        GermplasmList current = germplasmList.isFolder() ? germplasmList  : germplasmList.getParent();

		while (current != null && current.getId() != 0) {
			treeState.add(0, current.getId().toString());

			current = current.getParent();
		}

		treeState.add(0, GERMPLASM_LIST_ROOT_ITEM);

		return treeState;
	}

	/**
	 * Generates a list consisting of the list IDs representing the folders starting from the root leading up to the provided list
	 *
	 * @param sampleList
	 * @return List<String>
	 */
	protected List<String> computeTreeStateForSavedSampleListTree(final SampleList sampleList) {
		final List<String> treeState = new ArrayList<>();

		SampleList current = sampleList.isFolder() ? sampleList  : sampleList.getHierarchy();

		while (current != null && current.getId() != 0) {
			treeState.add(0, current.getId().toString());

			current = current.getHierarchy();
		}

		treeState.add(0, GERMPLASM_LIST_ROOT_ITEM);

		return treeState;
	}
}
