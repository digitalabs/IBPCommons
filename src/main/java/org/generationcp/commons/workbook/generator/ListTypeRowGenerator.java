
package org.generationcp.commons.workbook.generator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.WordUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Component;

@Component
public class ListTypeRowGenerator extends CodesSheetRowGenerator<UserDefinedField> {

	@Resource
	private GermplasmListManager germplasmListManager;

	@Override
	List<UserDefinedField> getSourceItem() {
		return this.germplasmListManager.getGermplasmListTypes();
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LIST_HEADER_STYLE);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
	}

	@Override
	String getSection() {
		return RowColumnType.LIST_TYPE.getSection();
	}

	@Override
	String getInfoType() {
		return RowColumnType.LIST_TYPE.toString();
	}

	@Override
	String getFcode(final UserDefinedField udField) {
		return udField.getFcode();
	}

	@Override
	String getFname(final UserDefinedField udField) {
		return WordUtils.capitalizeFully(udField.getFname());
	}
}
