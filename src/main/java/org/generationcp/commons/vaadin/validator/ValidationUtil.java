/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.vaadin.validator;

import com.vaadin.data.Validator.InvalidValueException;

public class ValidationUtil {

	/**
	 * Get a suitable message for the specified error.
	 * 
	 * @param error
	 * @return
	 */
	public static String getMessageFor(InvalidValueException error) {
		String message = error.getMessage();
		if (message == null && error.getCause() != null) {
			message = error.getCause().getMessage();
		} else if (message == null && error.getCauses() != null) {
			InvalidValueException[] causes = error.getCauses();
			if (causes.length > 0) {
				message = causes[0].getMessage();
			}
		}

		return message == null ? "" : message;
	}
}
