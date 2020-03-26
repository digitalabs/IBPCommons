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

import java.util.List;

public class Util {

	private Util() {
		// /private constructor
	}

	/**
	 * Get the boolean value of <code>value</code>.
	 * 
	 * @param value
	 * @return the boolean value of <code>value</code>. If <code>value</code> is null, this method returns false.
	 */
	public static boolean getValue(final Boolean value) {
		return Util.getValue(value, false);
	}

	public static boolean getValue(final Boolean value, final boolean defaultValue) {
		return value == null ? defaultValue : value;
	}

	/**
	 * Test whether <code>obj</code> is equal to one of the specified objects.
	 * 
	 * @param obj
	 * @param objs
	 * @return
	 */
	public static boolean isOneOf(final Object obj, final Object... objs) {
		if (objs == null) {
			return false;
		}

		for (final Object tmp : objs) {
			if (obj.equals(tmp)) {
				return true;
			} else if (obj instanceof String && obj.toString().equalsIgnoreCase(tmp.toString())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAllNull(final Object... args) {
		for (final Object obj : args) {
			if (obj != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Test whether <code>value</code> is equal to all of the specified values.
	 * 
	 * @param value
	 * @param values
	 * @return
	 */
	public static boolean isAllEqualTo(final Double value, final Double... values) {
		if (values == null) {
			return false;
		}

		for (final Double val : values) {
			if (!value.equals(val)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Test whether the specified list is "empty". A <code>null</code> value is considered "empty".
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(final List<?> list) {
		return list == null || list.isEmpty();
	}

	public static int max(final int value1, final int... values) {
		int max = value1;

		for (final int value : values) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}


}
