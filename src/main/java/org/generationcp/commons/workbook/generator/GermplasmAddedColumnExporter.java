package org.generationcp.commons.workbook.generator;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.parsing.ExcelWorkbookRow;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.interfaces.GermplasmExportSource;

public abstract class GermplasmAddedColumnExporter<SOURCE> {
	
	protected ExcelCellStyleBuilder sheetStyles;
	protected GermplasmListNewColumnsInfo columnsInfo;
	
	public Integer addRowsToDescriptionSheet(final HSSFSheet descriptionSheet, Integer startingRow, final ExcelCellStyleBuilder sheetStyles, final GermplasmListNewColumnsInfo columnsInfo) {
		this.sheetStyles = sheetStyles;
		this.columnsInfo = columnsInfo;
		final List<SOURCE> items = this.getSourceItems();
		final CellStyle labelStyle = this.getLabelStyle();
		final CellStyle dataStyle = this.getDataStyle();
		for (final SOURCE source : items) {
			final ExcelWorkbookRow itemRow = new ExcelWorkbookRow(descriptionSheet.createRow(++startingRow));
			itemRow.createCell(0, labelStyle, this.getName(source));
			itemRow.createCell(1, dataStyle, this.getDescription(source));
			itemRow.createCell(2, dataStyle, this.getProperty(source));
			itemRow.createCell(3, dataStyle, this.getScale(source));
			itemRow.createCell(4, dataStyle, this.getMethod(source));
			itemRow.createCell(5, dataStyle, this.getDatatype(source));
			itemRow.createCell(6, dataStyle, this.getValue(source));
			itemRow.createCell(7, dataStyle, this.getComments(source));
		}
		return startingRow;
	}
	
	public Integer generateAddedColumnValue(final HSSFRow row, final GermplasmExportSource data, final Integer startingColumnIndex){
		Integer columnIndex = startingColumnIndex;
		if (this.columnsInfo != null && !this.columnsInfo.getAddedColumnCurrentSort().isEmpty()) {
			for(final String column : this.columnsInfo.getAddedColumnCurrentSort()) {
				if (this.doIncludeColumn(column)) {
					final List<ListDataColumnValues> columnValues = this.columnsInfo.getColumnValuesMap().get(column);
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
		return  columnIndex;
	}

	public Integer generateAddedColumnHeader(final HSSFRow headerRow, final Integer startingColumnIndex){
		Integer columnIndex = startingColumnIndex;
		if (this.columnsInfo != null && !this.columnsInfo.getColumns().isEmpty()) {
			final List<SOURCE> items = this.getSourceItems();
			for (final SOURCE source : items) {
				final Cell entryTypeCell = headerRow.createCell(columnIndex);
				entryTypeCell.setCellValue(this.getName(source));
				entryTypeCell.setCellStyle(this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.HEADING_STYLE_FACTOR));
				columnIndex++;
			}
		}
		return  columnIndex;
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

	public void setSheetStyles(final ExcelCellStyleBuilder sheetStyles) {
		this.sheetStyles = sheetStyles;
	}

	public void setColumnsInfo(final GermplasmListNewColumnsInfo columnsInfo) {
		this.columnsInfo =  columnsInfo;
	}

}
