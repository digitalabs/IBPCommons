package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.service.ExportService;

import au.com.bytecode.opencsv.CSVWriter;

public class ExportServiceImpl implements ExportService{

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath) throws IOException {
		File newFile = new File(fileNameFullPath);
		CSVWriter writer = new CSVWriter(new FileWriter(newFile), ',', CSVWriter.NO_QUOTE_CHARACTER);
		// feed in your array (or convert your data to an array)
		List<String[]> rowValues = new ArrayList<String[]>();
		
		rowValues.add(getColumnHeaderNames(exportColumnHeaders));
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
		String[] strArray = values.toArray(new String[0]);
		return strArray;
	}
	
	protected String[] getColumnHeaderNames(List<ExportColumnHeader> exportColumnHeaders){
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < exportColumnHeaders.size(); i++) {
			ExportColumnHeader exportColumnHeader = exportColumnHeaders.get(i);
			if (exportColumnHeader.isDisplay()) {
				values.add(cleanNameValueCommas(exportColumnHeader.getName()));
			}
		}
		String[] strArray = values.toArray(new String[0]);
		return strArray;
	}
	
	protected String cleanNameValueCommas(String param){
		if(param != null) {
			return param.replaceAll(",", "_");
		}
		return "";
	}

}
