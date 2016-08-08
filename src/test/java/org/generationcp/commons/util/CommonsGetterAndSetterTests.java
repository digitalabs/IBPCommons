
package org.generationcp.commons.util;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ibp.test.utilities.TestGetterAndSetter;

/**
 * A helper test to test getter and setter solely for reducing noise in the test coverage.
 *
 */
public class CommonsGetterAndSetterTests extends TestSuite  {

	public static Test suite() {
		final TestGetterAndSetter TestGetterAndSetter = new TestGetterAndSetter();
		return TestGetterAndSetter.getTestSuite("CommonsGetterAndSetterTests", "org.generationcp.commons");
	}

}
