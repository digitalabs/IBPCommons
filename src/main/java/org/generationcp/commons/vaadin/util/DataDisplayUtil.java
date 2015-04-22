package org.generationcp.commons.vaadin.util;

public class DataDisplayUtil {

	/**
	 * Return a truncated string based on the maximum number of characters to
	 * display
	 * 
	 * @param input
	 *            - String to truncate
	 * @param maxLength
	 *            - maximum number to display
	 * @return
	 */
	public static String truncateDisplay(String input, int maxLength) {
		
		if (input == null){
			return "";
		}
		
		String truncatedString = input;
		if (maxLength > 0 && input.length() > maxLength) {
			truncatedString = truncatedString.substring(0, maxLength);
			truncatedString += "...";
		}

		return truncatedString;
	}
}
