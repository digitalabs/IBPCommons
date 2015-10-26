package org.generationcp.commons.workbook.generator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.regexp.recompile;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;

public class ListTypeRowGenerator extends CodesSheetRowGenerator<UserDefinedField> {

	@Resource
	private GermplasmDataManager germplasmDataManager;

	@Override
	List<UserDefinedField> getSourceItem() {
		return this.germplasmDataManager.getUserDefinedFieldByFieldTableNameAndType(RowColumnType.LIST_TYPE.getFtable(), RowColumnType.LIST_TYPE.getFtype());
	}

	@Override
	CellStyle getLabelStyle() {
		return sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LIST_HEADER_STYLE);
	}

	@Override
	CellStyle getDataStyle() {
		return sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
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
	String getFcode(UserDefinedField udField) {
		return udField.getFcode();
	}
	
	@Override
	String getFname(UserDefinedField udField) {
		return WordUtils.capitalizeFully(udField.getFname());
	}
}
