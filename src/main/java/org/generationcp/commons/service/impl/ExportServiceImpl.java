package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.service.ExportService;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportServiceImpl implements ExportService{
	
	private static final Logger LOG = LoggerFactory.getLogger(ExportServiceImpl.class);
	
	//List Details
	public static final String LIST_NAME = "LIST NAME";
	public static final String LIST_DESCRIPTION = "LIST DESCRIPTION";
	public static final String LIST_TYPE = "LIST TYPE";
	public static final String LIST_DATE = "LIST DATE";
	
	//Condition
	public static final String CONDITION = "CONDITION";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY = "PROPERTY";
	public static final String SCALE = "SCALE";
	public static final String METHOD = "METHOD";
	public static final String DATA_TYPE = "DATA TYPE";
	public static final String NESTED_IN = "NESTED IN";
	
	//Factor
	public static final String FACTOR = "FACTOR";
	
	//Values
	public static final String ASSIGNED = "ASSIGNED";
	public static final String PERSON = "PERSON";
	
	//Styles
	public static final String LABEL_STYLE = "labelStyle";
	public static final String HEADING_STYLE = "headingStyle";
	public static final String NUMERIC_STYLE = "numericStyle";
	private static final String TEXT_STYLE = "textStyle";
    
    //Columns
	public static final String ENTRY_ID = "entryId";
	public static final String GID = "gid";
	public static final String ENTRY_CODE = "entryCode";
	public static final String DESIGNATION = "desig";
	public static final String PARENTAGE = "parentage";
	public static final String SEED_SOURCE = "seedSource";

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath) throws IOException {
		return generateCSVFile(exportColumnValues, exportColumnHeaders, fileNameFullPath, true);
	}

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath,
			boolean includeHeader) throws IOException {
		File newFile = new File(fileNameFullPath);
		
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(
                new FileOutputStream(fileNameFullPath), "UTF-8"),
                ',', CSVWriter.NO_QUOTE_CHARACTER);

		//CSVWriter writer = new CSVWriter(fw, ',', CSVWriter.NO_QUOTE_CHARACTER);
		// feed in your array (or convert your data to an array)
		List<String[]> rowValues = new ArrayList<String[]>();
		if(includeHeader){
			rowValues.add(getColumnHeaderNames(exportColumnHeaders));
		}
		for(int i = 0 ; i < exportColumnValues.size() ; i++){
			rowValues.add(getColumnValues(exportColumnValues.get(i), exportColumnHeaders));
		}
		writer.writeAll(rowValues);
		writer.close();
		return newFile;
	}

	protected String[] getColumnValues(Map<Integer, ExportColumnValue> exportColumnMap,
			List<ExportColumnHeader> exportColumnHeaders) {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				ExportColumnValue exportColumnValue = exportColumnMap.get(exportColumnHeader.getId());
				String colName = "";
				if(exportColumnValue != null) {
					String value = exportColumnValue.getValue();
					colName = cleanNameValueCommas(value);
				}
				values.add(colName);
			}
		}
		return values.toArray(new String[0]);
	}
	
	protected String[] getColumnHeaderNames(List<ExportColumnHeader> exportColumnHeaders){
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				values.add(cleanNameValueCommas(exportColumnHeader.getName()));
			}
		}
		return values.toArray(new String[0]);
	}
	
	protected String cleanNameValueCommas(String param){
		if(param != null) {
			return param.replaceAll(",", "_");
		}
		return "";
	}

	@Override
	public FileOutputStream generateExcelFileForSingleSheet(
			List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String filename,
			String sheetName) throws IOException {
		
		HSSFWorkbook wb = createWorkbookForSingleSheet(exportColumnValues, exportColumnHeaders, sheetName);
        
        try {
            //write the excel file
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            wb.write(fileOutputStream);
            fileOutputStream.close();
            return fileOutputStream;
        } catch(IOException ex) {
            throw new IOException("Error with writing to: " + filename, ex);
        }
	}

	protected HSSFWorkbook createWorkbookForSingleSheet(
			List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String sheetName) {
		HSSFWorkbook wb = new HSSFWorkbook(); 
		HSSFSheet sheet = wb.createSheet(sheetName);
		
		int rowIndex = 0;
		writeColumHeaders(exportColumnHeaders, sheet, rowIndex);
		rowIndex++;
		
		rowIndex = writeColumnValues(exportColumnHeaders, exportColumnValues, sheet, rowIndex);
        
        for(int ctr = 0; ctr < rowIndex; ctr++) {
        	sheet.autoSizeColumn(rowIndex);
        }
		return wb;
	}

	protected int writeColumnValues(List<ExportColumnHeader> exportColumnHeaders, List<Map<Integer, ExportColumnValue>> exportColumnValues, 
				HSSFSheet sheet, int rowIndex) {
		int currentRowIndex = rowIndex;
		for(Map<Integer,ExportColumnValue> exportRowValue : exportColumnValues){
			HSSFRow row = sheet.createRow(currentRowIndex);
			
			int columnIndex = 0;
			for(ExportColumnHeader columnHeader : exportColumnHeaders){
				ExportColumnValue columnValue = exportRowValue.get(columnHeader.getId());
				row.createCell(columnIndex).setCellValue(columnValue.getValue());
				columnIndex++;
			}
			currentRowIndex++;
			
		}
		return currentRowIndex;
	}

	protected void writeColumHeaders(List<ExportColumnHeader> exportColumnHeaders, 
			HSSFSheet sheet, int rowIndex) {
		int noOfColumns = exportColumnHeaders.size();
		HSSFRow header = sheet.createRow(rowIndex);
		for(int i = 0; i < noOfColumns; i++){
			ExportColumnHeader columnHeader =  exportColumnHeaders.get(i);
			header.createCell(i).setCellValue(columnHeader.getName());
		}
	}

	@Override
	public FileOutputStream generateGermplasmListExcelFile(GermplasmListExportInputValues input) throws GermplasmListExporterException {
		
		//create workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        
        Map<String, CellStyle> sheetStyles = createStyles(wb);
        
        //create two worksheets - Description and Observations
        generateDescriptionSheet(wb,sheetStyles, input);
        generateObservationSheet(wb,sheetStyles, input); 
        
        String filename = input.getFileName();
        try {
            //write the excel file
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            wb.write(fileOutputStream);
            fileOutputStream.close();
            return fileOutputStream;
        } catch(Exception ex) {
        	LOG.error(ex.getMessage(),ex);
            throw new GermplasmListExporterException();
        }
	}

	protected int getNoOfVisibleColumns(Map<String, Boolean> visibleColumnMap) {
		int count = 0;
		for(Map.Entry<String, Boolean> column : visibleColumnMap.entrySet()){
			Boolean isVisible = column.getValue();
			if(isVisible){
				count++;
			}
		}
		return count;
	}

	protected void generateObservationSheet(HSSFWorkbook wb, Map<String, CellStyle> sheetStyles, 
				GermplasmListExportInputValues input) throws GermplasmListExporterException {
		
		HSSFSheet observationSheet = wb.createSheet("Observation");
		writeObservationSheet(sheetStyles, observationSheet, input);
		
		//adjust column widths of observation sheet to fit contents
		int noOfVisibleColumns = getNoOfVisibleColumns(input.getVisibleColumnMap());
        for(int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
            observationSheet.autoSizeColumn(ctr);
        }
	}

	@Override
	public void writeObservationSheet(Map<String, CellStyle> styles, HSSFSheet observationSheet,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {

    	Map<String, Boolean> visibleColumnMap = input.getVisibleColumnMap();
    	GermplasmList germplasmList = input.getGermplasmList(); 
    	List<GermplasmListData> listDatas = germplasmList.getListData();
    	
        createListEntriesHeaderRow(styles, observationSheet, visibleColumnMap);
        
        int i = 1;
        for (GermplasmListData listData : listDatas) {
            HSSFRow listEntry = observationSheet.createRow(i);
            
            int j = 0;
            if(visibleColumnMap.get(ENTRY_ID)){
            	listEntry.createCell(j).setCellValue(listData.getEntryId());
            	j++;
            }
            
            if(visibleColumnMap.get(GID)){
            	listEntry.createCell(j).setCellValue(listData.getGid());
            	j++;
            }
            
            if(visibleColumnMap.get(ENTRY_CODE)){
            	listEntry.createCell(j).setCellValue(listData.getEntryCode());
            	j++;
            }
            
            if(visibleColumnMap.get(DESIGNATION)){
            	listEntry.createCell(j).setCellValue(listData.getDesignation());
            	j++;
            }
            
            if(visibleColumnMap.get(PARENTAGE)){
            	listEntry.createCell(j).setCellValue(listData.getGroupName());
            	j++;
            }
            
            if(visibleColumnMap.get(SEED_SOURCE)){
            	listEntry.createCell(j).setCellValue(listData.getSeedSource());
            	j++;
            }
            
            i+=1;
        }
        
    }

	public void createListEntriesHeaderRow(Map<String, CellStyle> styles,
			HSSFSheet observationSheet, Map<String, Boolean> visibleColumnMap) {
		HSSFRow listEntriesHeader = observationSheet.createRow(0);
        
        int columnIndex = 0;
        if(visibleColumnMap.get(ENTRY_ID)){
        	Cell entryIdCell = listEntriesHeader.createCell(columnIndex);
            entryIdCell.setCellValue("ENTRY");
            entryIdCell.setCellStyle(styles.get(HEADING_STYLE));
            columnIndex++;
        }
        
        if(visibleColumnMap.get(GID)){
	        Cell gidCell = listEntriesHeader.createCell(columnIndex);
	        gidCell.setCellValue("GID");
	        gidCell.setCellStyle(styles.get(HEADING_STYLE));
	        columnIndex++;
        }
        
        if(visibleColumnMap.get(ENTRY_CODE)){
	        Cell entryCodeCell = listEntriesHeader.createCell(columnIndex);
	        entryCodeCell.setCellValue("ENTRY CODE");
	        entryCodeCell.setCellStyle(styles.get(HEADING_STYLE));
	        columnIndex++;
        }
        
        if(visibleColumnMap.get(DESIGNATION)){
	        Cell designationCell = listEntriesHeader.createCell(columnIndex);
	        designationCell.setCellValue("DESIGNATION");
	        designationCell.setCellStyle(styles.get(HEADING_STYLE));
	        columnIndex++;
        }
        
        if(visibleColumnMap.get(PARENTAGE)){
	        Cell crossCell = listEntriesHeader.createCell(columnIndex);
	        crossCell.setCellValue("CROSS");
	        crossCell.setCellStyle(styles.get(HEADING_STYLE));
	        columnIndex++;
        }
        
        if(visibleColumnMap.get(SEED_SOURCE)){
	        Cell sourceCell = listEntriesHeader.createCell(columnIndex);
	        sourceCell.setCellValue("SOURCE");
	        sourceCell.setCellStyle(styles.get(HEADING_STYLE));
	        columnIndex++;
        }
	}

	@Override
	public void generateDescriptionSheet(HSSFWorkbook wb, Map<String, CellStyle> sheetStyles,
			GermplasmListExportInputValues input) throws GermplasmListExporterException {
		
		HSSFSheet descriptionSheet = wb.createSheet("Description");
		 
		writeListDetailsSection(sheetStyles, descriptionSheet, 1, input.getGermplasmList());
        writeListConditionSection(sheetStyles, descriptionSheet, 6, input);
        writeListFactorSection(sheetStyles, descriptionSheet, 12, input.getVisibleColumnMap());
        
        //adjust column widths of description sheet to fit contents
        int noOfVisibleColumns = getNoOfVisibleColumns(input.getVisibleColumnMap());
        for(int ctr = 0; ctr < noOfVisibleColumns; ctr++) {
            descriptionSheet.autoSizeColumn(ctr);
        }
	}
	
	public void writeListFactorSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, 
    		int startingRow, Map<String, Boolean> visibleColumnMap) {
    	
        int actualRow = startingRow - 1;
        
        HSSFRow factorDetailsHeader = descriptionSheet.createRow(actualRow);
        Cell factorCell = factorDetailsHeader.createCell(0);
        factorCell.setCellValue(FACTOR);
        factorCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell descriptionCell = factorDetailsHeader.createCell(1);
        descriptionCell.setCellValue(DESCRIPTION);
        descriptionCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell propertyCell = factorDetailsHeader.createCell(2);
        propertyCell.setCellValue(PROPERTY);
        propertyCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell scaleCell = factorDetailsHeader.createCell(3);
        scaleCell.setCellValue(SCALE);
        scaleCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell methodCell = factorDetailsHeader.createCell(4);
        methodCell.setCellValue(METHOD);
        methodCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell dataTypeCell = factorDetailsHeader.createCell(5);
        dataTypeCell.setCellValue(DATA_TYPE);
        dataTypeCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell spaceCell = factorDetailsHeader.createCell(6);
        spaceCell.setCellValue(NESTED_IN);
        spaceCell.setCellStyle(styles.get(HEADING_STYLE));
        
        if(visibleColumnMap.get(ENTRY_ID)){
	        HSSFRow entryIdRow = descriptionSheet.createRow(++actualRow);
	        entryIdRow.createCell(0).setCellValue("ENTRY");
	        entryIdRow.createCell(1).setCellValue("The germplasm entry number");
	        entryIdRow.createCell(2).setCellValue("GERMPLASM ENTRY");
	        entryIdRow.createCell(3).setCellValue("NUMBER");
	        entryIdRow.createCell(4).setCellValue("ENUMERATED");
	        entryIdRow.createCell(5).setCellValue("N");
	        entryIdRow.createCell(6).setCellValue("");
        }
        
        if(visibleColumnMap.get(GID)){
        	HSSFRow gidRow = descriptionSheet.createRow(++actualRow);
            gidRow.createCell(0).setCellValue("GID");
            gidRow.createCell(1).setCellValue("The GID of the germplasm");
            gidRow.createCell(2).setCellValue("GERMPLASM ID");
            gidRow.createCell(3).setCellValue("DBID");
            gidRow.createCell(4).setCellValue(ASSIGNED);
            gidRow.createCell(5).setCellValue("N");
            gidRow.createCell(6).setCellValue("");
        }
        
        if(visibleColumnMap.get(ENTRY_CODE)){
        	HSSFRow entryCodeRow = descriptionSheet.createRow(++actualRow);
            entryCodeRow.createCell(0).setCellValue("ENTRY CODE");
            entryCodeRow.createCell(1).setCellValue("Germplasm entry code");
            entryCodeRow.createCell(2).setCellValue("GERMPLASM ENTRY");
            entryCodeRow.createCell(3).setCellValue("CODE");
            entryCodeRow.createCell(4).setCellValue(ASSIGNED);
            entryCodeRow.createCell(5).setCellValue("C");
            entryCodeRow.createCell(6).setCellValue("");
        }
        
        if(visibleColumnMap.get(DESIGNATION)){
        	HSSFRow designationRow = descriptionSheet.createRow(++actualRow);
            designationRow.createCell(0).setCellValue("DESIGNATION");
            designationRow.createCell(1).setCellValue("The name of the germplasm");
            designationRow.createCell(2).setCellValue("GERMPLASM ID");
            designationRow.createCell(3).setCellValue("DBCV");
            designationRow.createCell(4).setCellValue(ASSIGNED);
            designationRow.createCell(5).setCellValue("C");
            designationRow.createCell(6).setCellValue("");
        }
        
        if(visibleColumnMap.get(PARENTAGE)){
        	HSSFRow crossRow = descriptionSheet.createRow(++actualRow);
            crossRow.createCell(0).setCellValue("CROSS");
            crossRow.createCell(1).setCellValue("The pedigree string of the germplasm");
            crossRow.createCell(2).setCellValue("CROSS NAME");
            crossRow.createCell(3).setCellValue("NAME");
            crossRow.createCell(4).setCellValue(ASSIGNED);
            crossRow.createCell(5).setCellValue("C");
            crossRow.createCell(6).setCellValue("");
        }
        
        if(visibleColumnMap.get(SEED_SOURCE)){
        	HSSFRow sourceRow = descriptionSheet.createRow(++actualRow);
            sourceRow.createCell(0).setCellValue("SOURCE");
            sourceRow.createCell(1).setCellValue("The seed source of the germplasm");
            sourceRow.createCell(2).setCellValue("SEED SOURCE");
            sourceRow.createCell(3).setCellValue("NAME");
            sourceRow.createCell(4).setCellValue("Seed Source");
            sourceRow.createCell(5).setCellValue("C");
            sourceRow.createCell(6).setCellValue("");
        }
    }

	protected void writeListConditionSection(Map<String, CellStyle> styles, HSSFSheet descriptionSheet, 
    		int startingRow, GermplasmListExportInputValues input) throws GermplasmListExporterException {
    	
		//prepare inputs
		GermplasmList germplasmList = input.getGermplasmList();
		String ownerName = input.getOwnerName();
		String exporterName = input.getExporterName();
		Integer currentLocalIbdbUserId = input.getCurrentLocalIbdbUserId();
		
        int actualRow = startingRow - 1;
        
        // write user details
        HSSFRow conditionDetailsHeading = descriptionSheet.createRow(actualRow);
        Cell conditionCell = conditionDetailsHeading.createCell(0);
        conditionCell.setCellValue(CONDITION);
        conditionCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell descriptionCell = conditionDetailsHeading.createCell(1);
        descriptionCell.setCellValue(DESCRIPTION);
        descriptionCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell propertyCell = conditionDetailsHeading.createCell(2);
        propertyCell.setCellValue(PROPERTY);
        propertyCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell scaleCell = conditionDetailsHeading.createCell(3);
        scaleCell.setCellValue(SCALE);
        scaleCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell methodCell = conditionDetailsHeading.createCell(4);
        methodCell.setCellValue(METHOD);
        methodCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell dataTypeCell = conditionDetailsHeading.createCell(5);
        dataTypeCell.setCellValue(DATA_TYPE);
        dataTypeCell.setCellStyle(styles.get(HEADING_STYLE));
        Cell valueCell = conditionDetailsHeading.createCell(6);
        valueCell.setCellValue("VALUE");
        valueCell.setCellStyle(styles.get(HEADING_STYLE));
        
        HSSFRow listUserRow = descriptionSheet.createRow(actualRow + 1); 
        listUserRow.createCell(0).setCellValue("LIST USER");
        listUserRow.createCell(1).setCellValue("PERSON WHO MADE THE LIST");
        listUserRow.createCell(2).setCellValue(PERSON);
        listUserRow.createCell(3).setCellValue("DBCV");
        listUserRow.createCell(4).setCellValue(ASSIGNED);
        listUserRow.createCell(5).setCellValue("C");
        listUserRow.createCell(6).setCellValue(ownerName.trim());
        
        HSSFRow listUserIdRow = descriptionSheet.createRow(actualRow + 2); 
        listUserIdRow.createCell(0).setCellValue("LIST USER ID");
        listUserIdRow.createCell(1).setCellValue("ID OF LIST OWNER");
        listUserIdRow.createCell(2).setCellValue(PERSON);
        listUserIdRow.createCell(3).setCellValue("DBID");
        listUserIdRow.createCell(4).setCellValue(ASSIGNED);
        listUserIdRow.createCell(5).setCellValue("N");
        Cell userIdCell = listUserIdRow.createCell(6);
        userIdCell.setCellValue(germplasmList.getUserId());
        userIdCell.setCellStyle(styles.get(NUMERIC_STYLE));

        HSSFRow listExporterRow = descriptionSheet.createRow(actualRow + 3); 
        listExporterRow.createCell(0).setCellValue("LIST EXPORTER");
        listExporterRow.createCell(1).setCellValue("PERSON EXPORTING THE LIST");
        listExporterRow.createCell(2).setCellValue(PERSON);
        listExporterRow.createCell(3).setCellValue("DBCV");
        listExporterRow.createCell(4).setCellValue(ASSIGNED);
        listExporterRow.createCell(5).setCellValue("C");
        listExporterRow.createCell(6).setCellValue(exporterName.trim());
        
        HSSFRow listExporterIdRow = descriptionSheet.createRow(actualRow + 4); 
        listExporterIdRow.createCell(0).setCellValue("LIST EXPORTER ID");
        listExporterIdRow.createCell(1).setCellValue("ID OF LIST EXPORTER");
        listExporterIdRow.createCell(2).setCellValue(PERSON);
        listExporterIdRow.createCell(3).setCellValue("DBID");
        listExporterIdRow.createCell(4).setCellValue(ASSIGNED);
        listExporterIdRow.createCell(5).setCellValue("N");
        Cell localIdCell = listExporterIdRow.createCell(6);
        localIdCell.setCellValue(currentLocalIbdbUserId);
        localIdCell.setCellStyle(styles.get(NUMERIC_STYLE));
    }
	
	public void writeListDetailsSection(Map<String, CellStyle> styles, Sheet descriptionSheet,
			int startingRow, GermplasmList germplasmList) {
        int actualRow = startingRow - 1;
        
        Row nameRow = descriptionSheet.createRow(actualRow);
        descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow, actualRow, 1, 7));
        Cell nameLabel = nameRow.createCell(0);
        nameLabel.setCellValue(LIST_NAME); 
        nameLabel.setCellStyle(styles.get(LABEL_STYLE));
        
		Cell nameVal = nameRow.createCell(1);
		nameVal.setCellValue(germplasmList.getName());
		nameVal.setCellStyle(styles.get(TEXT_STYLE));

		Row titleRow = descriptionSheet.createRow(actualRow + 1);
        descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 1, actualRow + 1, 1, 7));
        Cell titleLabel = titleRow.createCell(0);
        titleLabel.setCellValue(LIST_DESCRIPTION);
        titleLabel.setCellStyle(styles.get(LABEL_STYLE));

		Cell titleVal = titleRow.createCell(1);
		titleVal.setCellValue(germplasmList.getDescription());
		titleVal.setCellStyle(styles.get(TEXT_STYLE));

		Row typeRow = descriptionSheet.createRow(actualRow + 2);
        descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 2, actualRow + 2, 1, 7));
        Cell typeLabel = typeRow.createCell(0);
        typeLabel.setCellValue(LIST_TYPE); 
        typeLabel.setCellStyle(styles.get(LABEL_STYLE));

		Cell typeVal = typeRow.createCell(1);
		typeVal.setCellValue(germplasmList.getType());
		typeVal.setCellStyle(styles.get(TEXT_STYLE));

		Row dateRow = descriptionSheet.createRow(actualRow + 3);
        descriptionSheet.addMergedRegion(new CellRangeAddress(actualRow + 3, actualRow + 3, 1, 7));
        Cell dateLabel = dateRow.createCell(0);
        dateLabel.setCellValue(LIST_DATE); 
        dateLabel.setCellStyle(styles.get(LABEL_STYLE));

		Cell dateCell = dateRow.createCell(1);
        dateCell.setCellValue(germplasmList.getDate());
        dateCell.setCellStyle(styles.get(NUMERIC_STYLE));
    }

	@Override
	public Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        
        // set cell style for labels in the description sheet
        CellStyle labelStyle = wb.createCellStyle();
		labelStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
        labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font labelFont = wb.createFont();
        labelFont.setColor(IndexedColors.WHITE.getIndex());
        labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        labelStyle.setFont(labelFont);
        styles.put(LABEL_STYLE, labelStyle);
        
        // set cell style for headings in the description sheet
        CellStyle headingStyle = wb.createCellStyle();
        headingStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headingFont = wb.createFont();
        headingFont.setColor(IndexedColors.WHITE.getIndex());
        headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headingStyle.setFont(headingFont);
        styles.put(HEADING_STYLE, headingStyle);
        
        //set cell style for numeric values (left alignment)
        CellStyle numericStyle = wb.createCellStyle();
        numericStyle.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put(NUMERIC_STYLE, numericStyle);
		
		CellStyle textStyle = wb.createCellStyle();
		textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		textStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styles.put(TEXT_STYLE,textStyle);

		return styles;
    }
	
}
