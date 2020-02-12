package org.generationcp.commons.workbook.generator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Component;

@Component
public class GermplasmAttributesWorkbookExporter extends GermplasmAddedColumnExporter<UserDefinedField> {

	@Resource
	private GermplasmDataManager germplasmManager;
	
	private List<String> addedAttributeColumns = new ArrayList<>();
	
	@Override
	List<UserDefinedField> getSourceItems() {
		final List<UserDefinedField> attributeTypeColumns = new ArrayList<>();
		//columnsInfo is null when exporting germplasm list from Study Manager
		if(this.columnsInfo != null) {
			final List<UserDefinedField> attributeTypes = this.germplasmManager.getAllAttributesTypes();
			final List<String> addedColumns = this.columnsInfo.getAddedColumnCurrentSort();
			for(final String addedCol : addedColumns) {
				final UserDefinedField field;
				field = attributeTypes.stream().filter(userDefinedField -> userDefinedField.getFcode().toUpperCase().equalsIgnoreCase(addedCol)).findFirst().get();
				if(field!=null){
					this.addedAttributeColumns.add(field.getFcode().toUpperCase());
					attributeTypeColumns.add(field);
				}
			}

		}
		return attributeTypeColumns;
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_VARIATE);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_STYLE);
	}

	@Override
	String getName(final UserDefinedField source) {
		return source.getFcode().toUpperCase();
	}

	@Override
	String getDescription(final UserDefinedField source) {
		return "Additional details about germplasm";
	}

	@Override
	String getProperty(final UserDefinedField source) {
		return "ATTRIBUTE";
	}

	@Override
	String getScale(final UserDefinedField source) {
		return "TEXT";
	}

	@Override
	String getMethod(final UserDefinedField source) {
		return "OBSERVED";
	}

	@Override
	String getValue(final UserDefinedField source) {
		return "";
	}

	@Override
	String getDatatype(final UserDefinedField source) {
		return "C";
	}

	@Override
	String getComments(final UserDefinedField source) {
		return "Optional";
	}

	@Override
	Boolean doIncludeColumn(final String column) {
		return this.addedAttributeColumns.contains(column);
	}

	public void setAddedAttributeColumns(final List<String> addedAttributeColumns) {
		this.addedAttributeColumns = addedAttributeColumns;
	}
	
}
