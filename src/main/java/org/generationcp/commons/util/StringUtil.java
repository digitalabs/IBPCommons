/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.commons.util;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Glenn Marintes
 */
public abstract class StringUtil {

	public static int parseInt(final String string, final int defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Integer parseInt(final String string, final Integer defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long parseLong(final String string, final long defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Long parseLong(final String string, final Long defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static float parseFloat(final String string, final float defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Float parseFloat(final String string, final Float defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	public static BigInteger parseBigInteger(final String string, final BigInteger defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return new BigInteger(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * A lenient version of {@link Double#parseDouble(String)}.
	 * 
	 * @param string
	 * @param defaultValue
	 * @return
	 */
	public static double parseDouble(final String string, final double defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * A lenient version of {@link Double#parseDouble(String)}.
	 * 
	 * @param string
	 * @param defaultValue
	 * @return <code>defaultValue</code> if <code>string</code> is blank or cannot be parsed as double. Returns the parsed double value
	 *         otherwise.
	 */
	public static Double parseDouble(final String string, final Double defaultValue) {
		if (StringUtils.isBlank(string)) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(string);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Join the specified list of objects with the specified delimiter. Any empty value in the list will be ignored.
	 * <p>
	 * If the specified <code>objectList</code> contains a {@link Collection} or an {@link Array}, its member objects will be recursively
	 * "joined".
	 * 
	 * @param delimiter
	 * @param objectList
	 * @return
	 */
	public static String joinIgnoreEmpty(final Object delimiter, final Object... objectList) {
		final StringBuilder sb = new StringBuilder();
		if (objectList == null) {
			return sb.toString();
		}

		for (final Object obj : objectList) {
			if (obj == null) {
				continue;
			}

			String value = null;
			if (obj instanceof Collection<?>) {
				value = StringUtil.joinIgnoreEmpty(delimiter, ((Collection<?>) obj).toArray());
			} else if (Object[].class.isInstance(obj)) {
				value = StringUtil.joinIgnoreEmpty(delimiter, (Object[]) obj);
			} else {
				value = obj.toString();
			}

			if (StringUtils.isEmpty(value)) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			sb.append(value);
		}

		return sb.toString();
	}

	/**
	 * Join the specified list of objects with the specified delimiter. Any null value in the list will be ignored.
	 * <p>
	 * If the specified <code>objectList</code> contains a {@link Collection} or an {@link Array}, its member objects will be recursively
	 * "joined".
	 * 
	 * @param delimiter
	 * @param objectList
	 * @return
	 */
	public static String joinIgnoreNull(final Object delimiter, final Object... objectList) {
		final StringBuilder sb = new StringBuilder();
		if (objectList == null) {
			return sb.toString();
		}

		for (final Object obj : objectList) {
			if (obj == null) {
				continue;
			}

			String value = null;
			if (obj instanceof Collection<?>) {
				value = StringUtil.joinIgnoreEmpty(delimiter, ((Collection<?>) obj).toArray());
			} else if (Object[].class.isInstance(obj)) {
				value = StringUtil.joinIgnoreEmpty(delimiter, (Object[]) obj);
			} else {
				value = obj.toString();
			}

			if (value == null) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			sb.append(value);
		}

		return sb.toString();
	}

	/**
	 * Prepend the {@link String} representation of the specified <code>obj</code> with the specified <code>prefix</code> until it reaches
	 * <code>maxLength</code>.
	 * 
	 * @param obj
	 * @param prefix
	 * @param maxLength
	 * @return
	 */
	public static String prependWith(final Object obj, final String prefix, final int maxLength) {
		if (obj == null) {
			return null;
		}
		if (prefix == null) {
			throw new IllegalArgumentException("prefix cannot be null");
		}

		String str = obj.toString();
		final int strLength = str.length();
		for (int i = strLength; i < maxLength; i++) {
			str = prefix + str;
		}

		return str;
	}

	/**
	 * Prepend the {@link String} representation of the specified <code>obj</code> with the specified <code>suffix</code> until it reaches
	 * <code>maxLength</code>.
	 * 
	 * @param obj
	 * @param suffix
	 * @param maxLength
	 * @return
	 */
	public static String appendWith(final Object obj, final String suffix, final int maxLength) {
		if (obj == null) {
			return null;
		}
		if (suffix == null) {
			throw new IllegalArgumentException("suffix cannot be null");
		}

		String str = obj.toString();
		final int strLength = str.length();
		for (int i = strLength; i < maxLength; i++) {
			str = str + suffix;
		}

		return str;
	}

	/**
	 * Create a {@link String} composed of <code>ch</code> concatenated <code>count</code> times.
	 * 
	 * @param ch
	 * @param count
	 * @return
	 */
	public static String stringOf(final char ch, final int count) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			sb.append(ch);
		}

		return sb.toString();
	}

	/**
	 * Create a {@link String} composed of <code>str</code> concatenated <code>count</code> times.
	 * 
	 * @param str
	 * @param count
	 * @return
	 */
	public static String stringOf(final String str, final int count) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++) {
			sb.append(str);
		}

		return sb.toString();
	}

	public static boolean isEmpty(final String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isEmptyOrWhitespaceOnly(final String s) {
		return StringUtil.isEmpty(s) || s.matches("\\A\\s*\\z");
	}

	public static String stringify(final Object[] values, final String delimiter) {
		final StringBuilder sb = new StringBuilder();
		for (final Object value : values) {
			if (sb.length() > 0) {
				sb.append(delimiter);
			}
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * Returns true if a given string can either be an Integer or a Double
	 * 
	 * @param any string
	 * @return true/false
	 */
	public static boolean isNumeric(final String value) {
		if (value != null) {
			return value.matches("[-+]?\\d*\\.?\\d+");
		}
		return false;
	}
}
