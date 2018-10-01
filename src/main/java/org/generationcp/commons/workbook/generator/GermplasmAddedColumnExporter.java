package org.generationcp.commons.workbook.generator;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.interfaces.GermplasmExportSource;

public abstract class GermplasmAddedColumnExporter<SOURCE> {
	
	protected ExcelCellStyleBuilder sheetStyles;
	protected GermplasmListNewColumnsInfo columnsInfo;
	
	public GermplasmAddedColumnExporter(ExcelCellStyleBuilder sheetStyles, GermplasmListNewColumnsInfo columnsInfo) {
		super();
		this.sheetStyles = sheetStyles;
		this.columnsInfo = columnsInfo;
	}

	public void addRowsToDescriptionSheet(final HSSFSheet descriptionSheet, final Integer startingRow) {
		final List<SOURCE> items = this.getSourceItems();
		final CellStyle labelStyle = this.getLabelStyle();
		final CellStyle dataStyle = this.getDataStyle();
		Integer currentRow = startingRow;
		for (final SOURCE source : items) {
			ExcelWorkbookRow itemRow = new ExcelWorkbookRow(descriptionSheet.createRow(currentRow));
			itemRow.createCell(0, labelStyle, getName(source));
			itemRow.createCell(1, dataStyle, getDescription(source));
			itemRow.createCell(2, dataStyle, getProperty(source));
			itemRow.createCell(3, dataStyle, getScale(source));
			itemRow.createCell(4, dataStyle, getMethod(source));
			itemRow.createCell(5, dataStyle, getDatatype(source));
			itemRow.createCell(6, dataStyle, getValue(source));
			itemRow.createCell(7, dataStyle, getComments(source));
			currentRow++;
		}
	}
	
	public void generateAddedColumnValue(final HSSFRow row, final GermplasmExportSource data, final Integer startingColumnIndex){
		Integer columnIndex = startingColumnIndex;
		if (columnsInfo != null && !columnsInfo.getColumns().isEmpty()) {
			for (final Map.Entry<String, List<ListDataColumnValues>> columnEntry : columnsInfo.getColumnValuesMap().entrySet()) {
				if (doIncludeColumn(columnEntry.getKey())) {
					columnEntry.getKey();
					final List<ListDataColumnValues> columnValues = columnEntry.getValue();
					final ListDataColumnValues listDataColumnValues =
							(ListDataColumnValues) CollectionUtils.find(columnValues, new org.apache.commons.collections.Predicate() {
								
								public boolean evaluate(final Object object) {
									return ((ListDataColumnValues) object).getListDataId().equals(data.getListDataId());
								}
							});
					final String value = (listDataColumnValues != null ? listDataColumnValues.getValue() : "");
					
					row.createCell(columnIndex).setCellValue(value);
					columnIndex++;
				}
			}
		}
	}
	
	public Boolean hasItems(){
		return !this.getSourceItems().isEmpty();
	}
	
	abstract List<SOURCE> getSourceItems();
	
	abstract CellStyle getLabelStyle();
	
	abstract CellStyle getDataStyle();
	
	abstract String getName(SOURCE source);
	
	abstract String getDescription(SOURCE source);
	
	abstract String getProperty(SOURCE source);
	
	abstract String getScale(SOURCE source);
	
	abstract String getMethod(SOURCE source);
	
	abstract String getValue(SOURCE source);
	
	abstract String getDatatype(SOURCE source);
	
	abstract String getComments(SOURCE source);
	
	abstract Boolean doIncludeColumn(String column);

}
