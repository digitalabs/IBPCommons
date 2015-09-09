
package org.generationcp.commons.service;

import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 1/28/2015 Time: 4:39 PM
 */
public interface CrossNameService {

	String getNextNameInSequence(CrossNameSetting setting) throws MiddlewareQueryException;

	Integer getNextNumberInSequence(CrossNameSetting setting) throws MiddlewareQueryException;

	String buildNextNameInSequence(Integer number, CrossNameSetting setting);
}
