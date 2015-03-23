/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.hibernate;

import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.Project;


/**
 * Provides methods for getting an instance of {@ ManagerFactory}.
 * 
 * @author Glenn Marintes
 */
public interface ManagerFactoryProvider {
    
    /**
     * Get a {@link ManagerFactory} setup to connect to the specified {@link Project}'s crop database. 
     * 
     * When done using the ManagerFactory, call {@link ManagerFactoryProvider#closeManagerFactory(ManagerFactory)} to
     * close the database connections used by the {@link ManagerFactory}.
     * 
     * @param project The project (program) for which ManagerFactory is needed.
     * @return The ManagerFactory for the given project.
     */
    public ManagerFactory getManagerFactoryForProject(Project project);
      
    /**
     * Close this ManagerFactoryProvider.<br>
     * Calling this method will close all ManagerFactory created.
     */
    public void close();
}
