
package org.generationcp.commons.data.initializer;

import org.generationcp.middleware.pojos.Person;

public class PersonDataInitializer {

	public static Person createPerson() {
		final Person person = new Person();
		person.setFirstName("Test");
		person.setMiddleName("");
		person.setLastName("Person");
		return person;
	}
}
