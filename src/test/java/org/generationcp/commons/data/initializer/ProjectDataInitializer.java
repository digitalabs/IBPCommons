package org.generationcp.commons.data.initializer;

import org.generationcp.middleware.pojos.workbench.Project;

public class ProjectDataInitializer {
	public static Project createProject(){
		Project project= new Project();
		project.setProjectId((long)1);
		return project;
	}
}
