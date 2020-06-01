package org.generationcp.commons.workbook.generator;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GermplasmNamesWorkbookExporter extends GermplasmAddedColumnExporter<UserDefinedField> {

	@Resource
	private GermplasmListManager germplasmListManager;
	
	private List<String> addedNameTypesColumns = new ArrayList<>();

	@Override
	List<UserDefinedField> getSourceItems() {
		final List<UserDefinedField> nameTypesColumns = new ArrayList<>();
		//columnsInfo is null when exporting germplasm list from Study Manager
		if(this.columnsInfo != null) {
			final List<UserDefinedField> nameTypes = this.germplasmListManager.getGermplasmNameTypes();
			final Map<String, UserDefinedField> namesTypesMap = nameTypes.stream().collect(Collectors.toMap(u -> u.getFname().toUpperCase(), u -> u, (u1, u2) -> u2));
			for (final String columnName : this.columnsInfo.getColumns()) {
				final UserDefinedField userDefinedField = namesTypesMap.get(columnName.toUpperCase());
				if (userDefinedField!=null) {
					this.addedNameTypesColumns.add(columnName.toUpperCase());
					nameTypesColumns.add(userDefinedField);
				}
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
	String getName(final UserDefinedField source) {
		// Use Fcode for the exported to be import-able in Germplasm Import - the tool
		// expects name type variables to use Fcode in first column
		return source.getFcode().toUpperCase();
	}

	@Override
	String getDescription(final UserDefinedField source) {
		return source.getFname().toUpperCase();
	}

	@Override
	String getProperty(final UserDefinedField source) {
		return "GERMPLASM ID";
	}

	@Override
	String getScale(final UserDefinedField source) {
		return "NAME";
	}

	@Override
	String getMethod(final UserDefinedField source) {
		return "ASSIGNED";
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
		return "See valid name types on Codes sheet for more options";
	}

	@Override
	Boolean doIncludeColumn(final String column) {
		return this.addedNameTypesColumns.contains(column);
	}

	public void setAddedNameTypesColumns(final List<String> addedNameTypesColumns) {
		this.addedNameTypesColumns = addedNameTypesColumns;
	}
}
