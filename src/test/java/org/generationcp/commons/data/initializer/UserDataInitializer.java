
package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.User;

public class UserDataInitializer {

	public static User createUser() {
		final User user = new User();
		user.setUserid(1);
		return user;
	}

	public static List<User> createUserList() {
		final List<User> users = new ArrayList<User>();
		users.add(UserDataInitializer.createUser());
		return users;
	}
}
