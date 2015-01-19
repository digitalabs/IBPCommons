package org.generationcp.commons.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;

public interface ExportService {
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath) throws IOException;
	
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath, boolean includeHeader) throws IOException;
	
	public FileOutputStream generateExcelFileForSingleSheet(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String filename, String sheetName) throws IOException;
	
	public FileOutputStream generateGermplasmListExcelFile(GermplasmListExportInputValues input) throws GermplasmListExporterException;
}
