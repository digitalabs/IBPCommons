
package org.generationcp.commons.reports.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.pojo.CustomReportType;
import org.generationcp.commons.reports.service.JasperReportService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.CustomReportTypeUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.util.CrossExpansionProperties;

public class JasperReportServiceImpl implements JasperReportService {

	@Resource
	private CrossExpansionProperties crossExpansionProperties;
	@Resource
	private ContextUtil contextUtil;
	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Override
	public List<CustomReportType> getCustomReportTypes(final String toolSection, final String toolName) {
		final List<CustomReportType> customReportTypes = new ArrayList<CustomReportType>();

		final Tool tool = this.workbenchDataManager.getToolWithName(toolName);
		final List<StandardPreset> standardPresetList =
				this.workbenchDataManager.getStandardPresetFromCropAndTool(this.contextUtil.getProjectInContext().getCropType()
						.getCropName().toLowerCase(), tool.getToolId().intValue(), toolSection);
		// we need to convert the standard preset for custom report type to custom report type pojo
		for (final StandardPreset standardPreset : standardPresetList) {
			customReportTypes.addAll(CustomReportTypeUtil.readReportConfiguration(standardPreset,
					this.crossExpansionProperties.getProfile()));
		}

		return customReportTypes;
	}

}
