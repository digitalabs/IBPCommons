package org.generationcp.commons.util;

/**
 * Created by Aldrin Batac on 6/3/16.
 */
public class BreedingViewUtil {

	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";

	/**
	 * Trim the spaces and replaces all Breeding View's invalid characters in a variable name with an underscore.
	 * Only alphanumeric (a-z A-Z), dash (-), underscore (_) and percentage (%) characters are allowed in Breeding View.
	 *
	 * @param name
	 * @return
	 */
	public static String trimAndSanitizeName(String name) {

		if (name != null) {
			return sanitizeName(name.trim());
		} else {
			return name;
		}

	}

	/**
	 * Replaces all Breeding View's invalid characters in a variable name with an underscore.
	 * Only alphanumeric (a-z A-Z), dash (-), underscore (_) and percentage (%) characters are allowed in Breeding View.
	 *
	 * @param name
	 * @return
	 */
	public static String sanitizeName(String name) {

		if (name != null) {
			return name.replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
		} else {
			return name;
		}

	}

}
