
package org.generationcp.commons.util;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.parsing.validation.BulkComplValidator;
import org.generationcp.commons.parsing.validation.ParsingValidator;
import org.generationcp.commons.parsing.validation.ValueTypeValidator;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testContainsInstance_False() {
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = this.createTestListWithOrWithoutClass(bulkComplClass, false);
		boolean containsInstance = Util.containsInstance(testList, bulkComplClass);
		Assert.assertFalse("Expected " + testList + " to not contain " + bulkComplClass.getName(), containsInstance);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testContainsInstance_True() {
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = this.createTestListWithOrWithoutClass(bulkComplClass, true);
		boolean containsInstance = Util.containsInstance(testList, bulkComplClass);
		Assert.assertTrue("Expected list to contain " + bulkComplClass.getName(), containsInstance);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testGetInstance_Null() {
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = this.createTestListWithOrWithoutClass(bulkComplClass, false);
		ParsingValidator validator = Util.getInstance(testList, bulkComplClass);
		Assert.assertNull("Expected list to not contain " + bulkComplClass.getName(), validator);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void testContainsInstance() {
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = this.createTestListWithOrWithoutClass(bulkComplClass, true);
		ParsingValidator validator = Util.getInstance(testList, bulkComplClass);
		Assert.assertNotNull("Expected list to contain " + bulkComplClass.getName(), validator);
	}

	@SuppressWarnings("rawtypes")
	private List<ParsingValidator> createTestListWithOrWithoutClass(Class classToInclude, boolean containsInstance) {
		List<ParsingValidator> validators = new ArrayList<ParsingValidator>();
		this.addToList(validators, new BulkComplValidator(8, 7), classToInclude, containsInstance);
		this.addToList(validators, new ValueTypeValidator(Integer.class), classToInclude, containsInstance);
		return validators;
	}

	@SuppressWarnings("rawtypes")
	private void addToList(List<ParsingValidator> validators, ParsingValidator validator, Class classToInclude, boolean containsInstance) {
		if (this.canAddToList(validator.getClass(), classToInclude, containsInstance)) {
			validators.add(validator);
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean canAddToList(Class currentClass, Class classToInclude, boolean containsInstance) {
		if (currentClass == classToInclude && containsInstance || currentClass != classToInclude && !containsInstance) {
			return true;
		}
		return false;
	}
}
