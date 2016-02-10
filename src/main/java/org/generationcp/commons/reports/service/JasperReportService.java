
package org.generationcp.commons.reports.service;

import java.util.List;

import org.generationcp.commons.pojo.CustomReportType;

public interface JasperReportService {

	/**
	 * Returns a list of custom report type
	 * 
	 * @param toolSection
	 * @param toolName
	 * @return
	 */
	List<CustomReportType> getCustomReportTypes(String toolSection, String toolName);

}
