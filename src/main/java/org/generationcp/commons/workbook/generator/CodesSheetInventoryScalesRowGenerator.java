
package org.generationcp.commons.workbook.generator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.service.api.OntologyService;
import org.springframework.stereotype.Component;

@Component
public class CodesSheetInventoryScalesRowGenerator extends CodesSheetRowGenerator<org.generationcp.middleware.domain.oms.Scale> {

	@Resource
	private OntologyService ontologyService;

	@Override
	List<Scale> getSourceItem(final String cropName) {
		return this.ontologyService.getAllInventoryScales();
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_INVENTORY);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
	}

	@Override
	String getSection() {
		return RowColumnType.SCALES_FOR_INVENTORY_UNITS.getSection();
	}

	@Override
	String getInfoType() {
		return RowColumnType.SCALES_FOR_INVENTORY_UNITS.toString();
	}

	@Override
	String getFcode(final Scale scale) {
		return scale.getName();
	}

	@Override
	String getFname(final Scale scale) {
		return scale.getDefinition();
	}

}
