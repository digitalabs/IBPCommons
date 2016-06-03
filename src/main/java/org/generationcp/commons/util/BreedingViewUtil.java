package org.generationcp.commons.util;

/**
 * Created by Aldrin Batac on 6/3/16.
 */
public class BreedingViewUtil {

	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";

	public static String trimAndSanitizeName(String name) {

		if (name != null) {
			return name.trim().replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
		} else {
			return name;
		}

	}

}
