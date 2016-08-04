package org.generationcp.commons.util;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by Aldrin Batac on 6/3/16.
 */
public class BreedingViewUtil {

	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";
	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS_ALPHA_NUMERIC_ONLY = "[^a-zA-Z0-9 -]+";
	public static final String REPLACEMENT_CHARACTER_FOR_INVALID_CHARACTERS = "_";

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
			String sanitized = name.replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, REPLACEMENT_CHARACTER_FOR_INVALID_CHARACTERS);
			// Remove the backslash character
			return sanitized.replaceAll("\\\\", REPLACEMENT_CHARACTER_FOR_INVALID_CHARACTERS);
		} else {
			return name;
		}

	}

	/**
	 * Replaces all invalid characters in a string with an underscore, only alphanumeric characters, spaces and dash (-) will be retained.
	 * Use this to sanitize analysis name and filenames in generating Breeding View's input files.
	 * @param name
	 * @return
	 */
	public static String sanitizeNameAlphaNumericOnly(String name) {

		if (name != null) {
			String sanitized = name.replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS_ALPHA_NUMERIC_ONLY, REPLACEMENT_CHARACTER_FOR_INVALID_CHARACTERS);
			// Remove the backslash character
			return sanitized.replaceAll("\\\\", REPLACEMENT_CHARACTER_FOR_INVALID_CHARACTERS);
		} else {
			return name;
		}

	}

}
