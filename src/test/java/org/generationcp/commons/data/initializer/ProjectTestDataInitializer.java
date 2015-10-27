
package org.generationcp.commons.data.initializer;

import org.generationcp.middleware.pojos.workbench.Project;

public class ProjectTestDataInitializer {

	public static Project createProject() {
		final Project project = new Project();
		project.setProjectId((long) 1);
		return project;
	}
}
