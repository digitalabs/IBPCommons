package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.UserDefinedField;

public class UserDefinedFieldDataInitializer {
	
	public static UserDefinedField createUserDefinedField(){
		UserDefinedField udField = new UserDefinedField();
		udField.setFcode("LST");
		udField.setFname("Generic List");
		return udField;
	}
	
	public static UserDefinedField createUserDefinedField(String fcode, String fname){
		UserDefinedField udField = new UserDefinedField();
		udField.setFcode(fcode);
		udField.setFname(fname);
		return udField;
	}
	
	public static List<UserDefinedField> createUserDefinedFieldList() {
		List<UserDefinedField> udFields = new ArrayList<UserDefinedField>();
		udFields.add(UserDefinedFieldDataInitializer.createUserDefinedField());
		return udFields;
	}
	
	public static List<UserDefinedField> createUserDefinedFieldList(String fcode, String fname) {
		List<UserDefinedField> udFields = new ArrayList<UserDefinedField>();
		udFields.add(UserDefinedFieldDataInitializer.createUserDefinedField(fcode, fname));
		return udFields;
	}
}
