package org.generationcp.commons.workbook.generator;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GermplasmAttributesWorkbookExporter extends GermplasmAddedColumnExporter<Variable> {

	private List<String> addedAttributeColumns = new ArrayList<>();

	@Override
	List<UserDefinedField> getSourceItems() {
		final List<UserDefinedField> attributeTypeColumns = new ArrayList<>();
		//columnsInfo is null when exporting germplasm list from Study Manager
		if (this.columnsInfo != null) {
			final List<UserDefinedField> attributeTypes = this.germplasmManager.getAllAttributesTypes();
			final Map<String, UserDefinedField>
				attributeTypesMap = attributeTypes.stream().collect(Collectors.toMap(u -> u.getFcode().toUpperCase(), u -> u, (u1, u2) -> u1));

			for (final String addedCol : this.columnsInfo.getColumns()) {
				final UserDefinedField field = attributeTypesMap.get(addedCol.toUpperCase());
				if (field != null) {
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
