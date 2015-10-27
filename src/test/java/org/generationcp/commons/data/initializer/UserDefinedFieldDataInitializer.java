
package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.UserDefinedField;

public class UserDefinedFieldDataInitializer {

	public static UserDefinedField createUserDefinedField() {
		final UserDefinedField udField = new UserDefinedField();
		udField.setFcode("LST");
		udField.setFname("Generic List");
		return udField;
	}

	public static UserDefinedField createUserDefinedField(final String fcode, final String fname) {
		final UserDefinedField udField = new UserDefinedField();
		udField.setFcode(fcode);
		udField.setFname(fname);
		return udField;
	}

	public static List<UserDefinedField> createUserDefinedFieldList() {
		final List<UserDefinedField> udFields = new ArrayList<UserDefinedField>();
		udFields.add(UserDefinedFieldDataInitializer.createUserDefinedField());
		return udFields;
	}

	public static List<UserDefinedField> createUserDefinedFieldList(final String fcode, final String fname) {
		final List<UserDefinedField> udFields = new ArrayList<UserDefinedField>();
		udFields.add(UserDefinedFieldDataInitializer.createUserDefinedField(fcode, fname));
		return udFields;
	}
}
