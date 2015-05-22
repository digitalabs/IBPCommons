package org.generationcp.commons.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.parsing.validation.BulkComplValidator;
import org.generationcp.commons.parsing.validation.ParsingValidator;
import org.generationcp.commons.parsing.validation.ValueTypeValidator;
import org.junit.Test;

public class UtilTest {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContainsInstance_False(){
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = createTestListWithOrWithoutClass(
				bulkComplClass, false);
		boolean containsInstance = Util.containsInstance(testList, bulkComplClass);
		assertFalse("Expected "+ testList + " to not contain "+bulkComplClass.getName(),
				containsInstance);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContainsInstance_True(){
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = createTestListWithOrWithoutClass(
				bulkComplClass, true);
		boolean containsInstance = Util.containsInstance(testList, bulkComplClass);
		assertTrue("Expected list to contain "+bulkComplClass.getName(),
				containsInstance);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetInstance_Null(){
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = createTestListWithOrWithoutClass(
				bulkComplClass, false);
		ParsingValidator validator = Util.getInstance(testList, bulkComplClass);
		assertNull("Expected list to not contain "+bulkComplClass.getName(),
				validator);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testContainsInstance(){
		Class bulkComplClass = BulkComplValidator.class;
		List<ParsingValidator> testList = createTestListWithOrWithoutClass(
				bulkComplClass, true);
		ParsingValidator validator = Util.getInstance(testList, bulkComplClass);
		assertNotNull("Expected list to contain "+bulkComplClass.getName(),
				validator);
	}

	@SuppressWarnings("rawtypes")
	private List<ParsingValidator> createTestListWithOrWithoutClass(
			Class classToInclude, boolean containsInstance) {
		List<ParsingValidator> validators = new ArrayList<ParsingValidator>();
		addToList(validators,new BulkComplValidator(8, 7),classToInclude,containsInstance);
		addToList(validators,new ValueTypeValidator(Integer.class),classToInclude,containsInstance);
		return validators;
	}

	@SuppressWarnings("rawtypes")
	private void addToList(List<ParsingValidator> validators, ParsingValidator validator,
			Class classToInclude, boolean containsInstance) {
		if(canAddToList(validator.getClass(),classToInclude,containsInstance)) {
			validators.add(validator);
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean canAddToList(Class currentClass, Class classToInclude, boolean containsInstance) {
		if((currentClass == classToInclude && containsInstance) ||
			(currentClass != classToInclude && !containsInstance)) {
			return true;
		}
		return false;
	}
}
