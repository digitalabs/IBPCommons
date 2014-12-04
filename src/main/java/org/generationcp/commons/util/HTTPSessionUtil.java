package org.generationcp.commons.util;

import javax.servlet.http.HttpSession;

/**
 * Created by cyrus on 11/24/14.
 */
public class HTTPSessionUtil {
	/*
		 * this is being use for caching possible values, mainly use when creating Nursery (like breeding method, other categorical variates),
		 * we would want to clear this session every time we enter the create screen so we are sure it would get new value in the db
		 */
	public static final String POSSIBLE_VALUES_SESSION_NAME = "scopedTarget.possibleValuesCache";
	/*
	 * This is the session object being use for advancing a study
	 */
	public static final String ADVANCING_NURSERY_SESSION_NAME = "scopedTarget.advancingNursery";
	/*
	 * This is the session object being use for create/editing a nursery
	 */
	public static final String USER_SELECTION_SESSION_NAME = "scopedTarget.userSelection";
	/*
	 * This is the session object being use for seed inventory
	 */
	public static final String SEED_SELECTION_SESSION_NAME = "scopedTarget.seedSelection";
	/*
	 * This is the session object being use for fieldmap process
	 */
	public static final String FIELDMAP_SESSION_NAME = "scopedTarget.userFieldmap";
	/*
	 * This is the session object being use for label printing
	 */
	public static final String LABEL_PRINTING_SESSION_NAME = "scopedTarget.userLabelPrinting";
	/*
	 * This is the session object being use to store paginated pages like the one being use in the review page
	 */
	public static final String PAGINATION_LIST_SELECTION_SESSION_NAME = "scopedTarget.paginationListSelection";



	//this would be use in place for the session.invalidate
	public void clearSessionData(HttpSession session, String[] attributeNames){
		if(session != null && attributeNames != null){
			for(int index = 0 ; index < attributeNames.length ; index++){
				session.removeAttribute(attributeNames[index]);
			}
		}
	}

}
