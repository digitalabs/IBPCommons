
package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.parsing.GermplasmExportedWorkbook;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.service.FileService;
import org.generationcp.commons.service.GermplasmExportService;
import org.generationcp.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * see {@link org.generationcp.commons.service.GermplasmExportService} documentation
 */
public class GermplasmExportServiceImpl implements GermplasmExportService {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmExportServiceImpl.class);

	@Resource
	private FileService fileService;

	private String templateFile;

	@Override
	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath) throws IOException {
		return this.generateCSVFile(exportColumnValues, exportColumnHeaders, fileNameFullPath, true);
	}

	@Override
	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath, final boolean includeHeader)
			throws IOException {
		final File newFile = new File(fileNameFullPath);

		final CSVWriter writer =
				new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileNameFullPath), "UTF-8"), ',', CSVWriter.NO_QUOTE_CHARACTER);

		// feed in your array (or convert your data to an array)
		final List<String[]> rowValues = new ArrayList<>();
		if (includeHeader) {
			rowValues.add(this.getColumnHeaderNames(exportColumnHeaders));
		}
		for (final Map<Integer, ExportColumnValue> exportColumnValue : exportColumnValues) {
			rowValues.add(this.getColumnValues(exportColumnValue, exportColumnHeaders));
		}
		writer.writeAll(rowValues);
		writer.close();
		return newFile;
	}

	protected String[] getColumnValues(final Map<Integer, ExportColumnValue> exportColumnMap,
			final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<>();
		for (final ExportColumnHeader exportColumnHeader : exportColumnHeaders) {
			if (exportColumnHeader.isDisplay()) {
				final ExportColumnValue exportColumnValue = exportColumnMap.get(exportColumnHeader.getId());
				String colName = "";
				if (exportColumnValue != null) {
					final String value = exportColumnValue.getValue();
					colName = StringUtil.cleanNameValueCommas(value);
				}
				values.add(colName);
			}
		}
		return values.toArray(new String[values.size()]);
	}

	protected String[] getColumnHeaderNames(final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<>();
		for (final ExportColumnHeader exportColumnHeader : exportColumnHeaders) {
			if (exportColumnHeader.isDisplay()) {
				values.add(StringUtil.cleanNameValueCommas(exportColumnHeader.getName()));
			}
		}
		return values.toArray(new String[values.size()]);
	}

	@Override
	public FileOutputStream generateExcelFileForSingleSheet(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String filename, final String sheetName) throws IOException {

		final HSSFWorkbook wb = this.createWorkbookForSingleSheet(exportColumnValues, exportColumnHeaders, sheetName);

		try {
			// write the excel file
			final FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (final IOException ex) {
			throw new IOException("Error with writing to: " + filename, ex);
		}
	}

	protected HSSFWorkbook createWorkbookForSingleSheet(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String sheetName) {
		final HSSFWorkbook wb = new HSSFWorkbook();
		final HSSFSheet sheet = wb.createSheet(sheetName);

		int rowIndex = 0;
		this.writeColumHeaders(exportColumnHeaders, wb, sheet, rowIndex);
		rowIndex++;

		rowIndex = this.writeColumnValues(exportColumnHeaders, exportColumnValues, sheet, rowIndex);

		for (int ctr = 0; ctr < rowIndex; ctr++) {
			sheet.autoSizeColumn(rowIndex);
		}
		return wb;
	}

	protected int writeColumnValues(final List<ExportColumnHeader> exportColumnHeaders,
			final List<Map<Integer, ExportColumnValue>> exportColumnValues, final HSSFSheet sheet, final int rowIndex) {
		int currentRowIndex = rowIndex;
		for (final Map<Integer, ExportColumnValue> exportRowValue : exportColumnValues) {
			final HSSFRow row = sheet.createRow(currentRowIndex);

			int columnIndex = 0;
			for (final ExportColumnHeader columnHeader : exportColumnHeaders) {
				final ExportColumnValue columnValue = exportRowValue.get(columnHeader.getId());
				row.createCell(columnIndex).setCellValue(columnValue.getValue());
				columnIndex++;
			}
			currentRowIndex++;

		}
		return currentRowIndex;
	}

	protected void writeColumHeaders(final List<ExportColumnHeader> exportColumnHeaders, final HSSFWorkbook xlsBook, final HSSFSheet sheet,
			final int rowIndex) {
		final int noOfColumns = exportColumnHeaders.size();
		final HSSFRow header = sheet.createRow(rowIndex);
		final CellStyle greenBg = this.getHeaderStyle(xlsBook, 51, 153, 102);
		final CellStyle blueBg = this.getHeaderStyle(xlsBook, 51, 51, 153);
		for (int i = 0; i < noOfColumns; i++) {
			final ExportColumnHeader columnHeader = exportColumnHeaders.get(i);
			final Cell cell = header.createCell(i);
			if (columnHeader.getHeaderColor() != null) {
				if (columnHeader.getHeaderColor().intValue() == ExportColumnHeader.GREEN.intValue()) {
					cell.setCellStyle(greenBg);
				} else if (columnHeader.getHeaderColor().intValue() == ExportColumnHeader.BLUE.intValue()) {
					cell.setCellStyle(blueBg);
				}
			}
			cell.setCellValue(columnHeader.getName().toUpperCase());
		}
	}

	private CellStyle getHeaderStyle(final HSSFWorkbook xlsBook, final int c1, final int c2, final int c3) {
		final HSSFPalette palette = xlsBook.getCustomPalette();
		final HSSFColor color = palette.findSimilarColor(c1, c2, c3);
		final short colorIndex = color.getIndex();

		final HSSFFont whiteFont = xlsBook.createFont();
		whiteFont.setColor(new HSSFColor.WHITE().getIndex());

		final CellStyle cellStyle = xlsBook.createCellStyle();
		cellStyle.setFillForegroundColor(colorIndex);
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setFont(whiteFont);

		return cellStyle;
	}

	/**
	 * Main workbook generation entry point. Uses the GermplasmExportedWorkbook class to
	 * build an Excel style workbook to export.
	 */
	@Override
	public FileOutputStream generateGermplasmListExcelFile(final GermplasmListExportInputValues input)
			throws GermplasmListExporterException {

		// create workbook
		final GermplasmExportedWorkbook wb;
		try {
			final HSSFWorkbook hssWb = (HSSFWorkbook) this.fileService.retrieveWorkbookTemplate(this.templateFile);
			wb = new GermplasmExportedWorkbook(hssWb, input);
		} catch (InvalidFormatException | IOException e) {
			GermplasmExportServiceImpl.LOG.error(e.getMessage(), e);
			throw new GermplasmListExporterException();
		}

		final String filename = input.getFileName();
		try {
			// write the excel file
			final FileOutputStream fileOutputStream = new FileOutputStream(filename);
			wb.write(fileOutputStream);
			fileOutputStream.close();
			return fileOutputStream;
		} catch (final Exception ex) {
			GermplasmExportServiceImpl.LOG.error(ex.getMessage(), ex);
			throw new GermplasmListExporterException();
		}
	}


	public void setTemplateFile(final String templateFile) {
		this.templateFile = templateFile;
	}


}
