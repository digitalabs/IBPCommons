/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SheetUtil {

	private static final Logger LOG = LoggerFactory.getLogger(SheetUtil.class);

	/**
	 * Given a sheet, this method deletes a column from a sheet and moves all the columns to the right of it to the left one cell.
	 *
	 * Note, this method will not update any formula references.
	 *
	 * @param sheet
	 * @param column
	 */
	public static void deleteColumn(Sheet sheet, int columnToDelete) {
		int maxColumn = 0;
		for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
			Row row = sheet.getRow(r);

			// if no row exists here; then nothing to do; next!
			if (row == null) {
				continue;
			}

			// if the row doesn't have this many columns then we are good; next!
			int lastColumn = row.getLastCellNum();
			if (lastColumn > maxColumn) {
				maxColumn = lastColumn;
			}

			if (lastColumn < columnToDelete) {
				continue;
			}

			for (int x = columnToDelete + 1; x < lastColumn + 1; x++) {
				Cell oldCell = row.getCell(x - 1);
				if (oldCell != null) {
					row.removeCell(oldCell);
				}

				Cell nextCell = row.getCell(x);
				if (nextCell != null) {
					Cell newCell = row.createCell(x - 1, nextCell.getCellType());
					SheetUtil.cloneCell(newCell, nextCell);
				}
			}
		}

		// Adjust the column widths
		for (int c = 0; c < maxColumn; c++) {
			sheet.setColumnWidth(c, sheet.getColumnWidth(c + 1));
		}
	}

	/*
	 * Takes an existing Cell and merges all the styles and forumla into the new one
	 */
	private static void cloneCell(Cell cNew, Cell cOld) {
		cNew.setCellComment(cOld.getCellComment());
		cNew.setCellStyle(cOld.getCellStyle());

		switch (cNew.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN: {
				cNew.setCellValue(cOld.getBooleanCellValue());
				break;
			}
			case Cell.CELL_TYPE_NUMERIC: {
				cNew.setCellValue(cOld.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_STRING: {
				cNew.setCellValue(cOld.getStringCellValue());
				break;
			}
			case Cell.CELL_TYPE_ERROR: {
				cNew.setCellValue(cOld.getErrorCellValue());
				break;
			}
			case Cell.CELL_TYPE_FORMULA: {
				cNew.setCellFormula(cOld.getCellFormula());
				break;
			}
		}
	}

	public static String[] sheetsToArray(Workbook workbook) {

		List<String> contents = new ArrayList<String>();
		int index = 0;
		Sheet sheet = workbook.getSheetAt(index);
		String cellvalue = "";
		try {
			while (sheet != null) {
				cellvalue = "";
				try {
					cellvalue = sheet.getSheetName();
					if (cellvalue == null) {
						cellvalue = "";
					}
				} catch (Exception ec) {

				}
				contents.add(cellvalue);
				index++;
				sheet = workbook.getSheetAt(index);
			}
		} catch (Exception e) {
			SheetUtil.LOG.error(e.getMessage(), e);
		}
		return contents.toArray(new String[0]);
	}
}
