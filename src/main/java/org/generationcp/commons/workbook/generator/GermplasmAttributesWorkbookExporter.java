package org.generationcp.commons.workbook.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
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
			final Set<String> addedColumns = this.columnsInfo.getColumns();
			for (final UserDefinedField field : attributeTypes) {
				final String attributeTypeCode = field.getFcode().toUpperCase();
				if (addedColumns.contains(attributeTypeCode)) {
					this.addedAttributeColumns.add(attributeTypeCode);
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
	String getName(UserDefinedField source) {
		return source.getFcode().toUpperCase();
	}

	@Override
	String getDescription(UserDefinedField source) {
		return "Additional details about germplasm";
	}

	@Override
	String getProperty(UserDefinedField source) {
		return "ATTRIBUTE";
	}

	@Override
	String getScale(UserDefinedField source) {
		return "TEXT";
	}

	@Override
	String getMethod(UserDefinedField source) {
		return "OBSERVED";
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
		return "Optional";
	}

	@Override
	Boolean doIncludeColumn(String column) {
		return this.addedAttributeColumns.contains(column);
	}

	public void setAddedAttributeColumns(List<String> addedAttributeColumns) {
		this.addedAttributeColumns = addedAttributeColumns;
	}
	
}
