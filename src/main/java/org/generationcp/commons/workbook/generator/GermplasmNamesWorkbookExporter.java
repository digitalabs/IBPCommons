package org.generationcp.commons.workbook.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.UserDefinedField;


public class GermplasmNamesWorkbookExporter extends GermplasmAddedColumnExporter<UserDefinedField> {

	@Resource
	private GermplasmListManager germplasmListManager;
	
	private List<String> addedNameTypesColumns = new ArrayList<>();
	
	public GermplasmNamesWorkbookExporter(final ExcelCellStyleBuilder sheetStyles, final GermplasmListNewColumnsInfo columnsInfo) {
		super(sheetStyles, columnsInfo);
	}

	@Override
	List<UserDefinedField> getSourceItems() {
		final List<UserDefinedField> nameTypes = this.germplasmListManager.getGermplasmNameTypes();
		final List<UserDefinedField> nameTypesColumns = new ArrayList<>();
		final Set<String> addedColumns = this.columnsInfo.getColumns();
		for (final UserDefinedField field : nameTypes) {
			final String nameType = field.getFname().toUpperCase();
			if (addedColumns.contains(nameType)) {
				addedNameTypesColumns.add(nameType);
				nameTypesColumns.add(field);
			}
		}
		return nameTypesColumns;
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_FACTOR);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_STYLE);
	}

	@Override
	String getName(UserDefinedField source) {
		// Use Fcode for the exported to be import-able in Germplasm Import - the tool
		// expects name type variables to use Fcode in first column
		return source.getFcode().toUpperCase();
	}

	@Override
	String getDescription(UserDefinedField source) {
		return source.getFname().toUpperCase();
	}

	@Override
	String getProperty(UserDefinedField source) {
		return "GERMPLASM ID";
	}

	@Override
	String getScale(UserDefinedField source) {
		return "NAME";
	}

	@Override
	String getMethod(UserDefinedField source) {
		return "ASSIGNED";
	}

	@Override
	String getValue(UserDefinedField source) {
		return "";
	}

	@Override
	String getDatatype(UserDefinedField source) {
		return "C";
	}

	@Override
	String getComments(UserDefinedField source) {
		return "See valid name types on Codes sheet for more options";
	}

	@Override
	Boolean doIncludeColumn(String column) {
		return this.addedNameTypesColumns.contains(column);
	}
	
}
