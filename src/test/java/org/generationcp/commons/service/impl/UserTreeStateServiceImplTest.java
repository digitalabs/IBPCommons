
package org.generationcp.commons.service.impl;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.service.UserTreeStateService;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
@RunWith(MockitoJUnitRunner.class)
public class UserTreeStateServiceImplTest {

	private static final Integer TEST_USER_ID = 2;
	private static final String TEST_PROGRAM_UUID = "test";
	private static final Integer FIRST_LEVEL_FOLDER_ID = 1;
	private static final Integer SECOND_LEVEL_FOLDER_ID = 2;
	private static final Integer TEST_LIST_ID = 3;

	@Mock
	private UserProgramStateDataManager userProgramStateDataManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@InjectMocks
	private UserTreeStateServiceImpl unitUnderTest;

	@Test
	public void testRetrieveSaveListNoItemsSaved() {
		final List<String> savedNavigationState = new LinkedList<>();
        savedNavigationState.add(UserTreeStateServiceImpl.GERMPLASM_LIST_ROOT_ITEM);
        savedNavigationState.add(FIRST_LEVEL_FOLDER_ID.toString());

		// receiving null when retrieving for last saved germplasm list triggers the implem to retrieve the user's last stored navigation
		// tree state
		Mockito.when(germplasmListManager.getLastSavedGermplasmListByUserId(TEST_USER_ID, TEST_PROGRAM_UUID)).thenReturn(null);
		Mockito.when(
				userProgramStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(TEST_USER_ID, TEST_PROGRAM_UUID,
						ListTreeState.GERMPLASM_LIST.name())).thenReturn(savedNavigationState);

		// the expected tree state is similar to the saved navigation state, except that it should have a marker at the start to indicate that results
        // are based on the user's past navigation
		final List<String> expectedTreeState =
				Arrays.asList(UserTreeStateService.USE_PREVIOUS_NAVIGATION_MARKER,
                        UserTreeStateServiceImpl.GERMPLASM_LIST_ROOT_ITEM, FIRST_LEVEL_FOLDER_ID.toString());

		final List<String> retrievedState = unitUnderTest.getUserProgramTreeStateForSaveList(TEST_USER_ID, TEST_PROGRAM_UUID);
		Assert.assertEquals("Service must retrieve the latest tree navigation if no lists have been saved currently", expectedTreeState,
				retrievedState);
	}

	@Test
    public void testRetrieveSavedListItemsGermplasmList() {
        testRetrieveSaveListItemsPreviouslySaved(GermplasmList.LIST_TYPE);
    }

	void testRetrieveSaveListItemsPreviouslySaved(final String listType) {
		Mockito.when(germplasmListManager.getLastSavedGermplasmListByUserId(TEST_USER_ID, TEST_PROGRAM_UUID)).thenReturn(
				constructDummyNestedGermplasmListFolder(listType));

		final List<String> expectedTreeState =
				Arrays.asList(UserTreeStateService.USE_LAST_SAVED_MARKER, UserTreeStateServiceImpl.GERMPLASM_LIST_ROOT_ITEM,
                        FIRST_LEVEL_FOLDER_ID.toString(), SECOND_LEVEL_FOLDER_ID.toString());

		final List<String> actualState = unitUnderTest.getUserProgramTreeStateForSaveList(TEST_USER_ID, TEST_PROGRAM_UUID);

		Assert.assertEquals(
				"Service must generate a state starting from the marker, then the list item name, followed by the folder IDs of the containing folders",
				expectedTreeState, actualState);
	}

	private GermplasmList constructDummyNestedGermplasmListFolder(final String listType) {
		final GermplasmList firstLevel = new GermplasmList(FIRST_LEVEL_FOLDER_ID);
		firstLevel.setType(GermplasmList.FOLDER_TYPE);
		final GermplasmList secondLevel = new GermplasmList(SECOND_LEVEL_FOLDER_ID);
		secondLevel.setType(GermplasmList.FOLDER_TYPE);
		secondLevel.setParent(firstLevel);

		final GermplasmList actualList = new GermplasmList(TEST_LIST_ID);
		actualList.setType(listType);
		actualList.setParent(secondLevel);

		return actualList;
	}
}
