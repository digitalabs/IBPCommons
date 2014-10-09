package org.generationcp.commons.util;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;

public class UserUtil {

	public static int getCurrentUserLocalId(WorkbenchDataManager workbenchDataManager) throws MiddlewareQueryException {
        WorkbenchRuntimeData runtimeData = workbenchDataManager.getWorkbenchRuntimeData(); 
		Integer workbenchUserId = runtimeData.getUserId();
        Project lastProject = workbenchDataManager.getLastOpenedProject(workbenchUserId);
        Integer localIbdbUserId = workbenchDataManager.getLocalIbdbUserId(workbenchUserId,lastProject.getProjectId());
        if (localIbdbUserId != null) {
            return localIbdbUserId;
        } else {
            return -1; 
        }
    }
	
}
