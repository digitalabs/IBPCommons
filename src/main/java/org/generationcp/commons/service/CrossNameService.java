
package org.generationcp.commons.service;

import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface CrossNameService {

	String getNextNameInSequence(CrossNameSetting setting) throws MiddlewareException;

	Integer getNextNumberInSequence(CrossNameSetting setting) throws MiddlewareQueryException;

	String buildNextNameInSequence(Integer number, CrossNameSetting setting);
}
