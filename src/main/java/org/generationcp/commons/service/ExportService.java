package org.generationcp.commons.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;

public interface ExportService {
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileNameFullPath) throws IOException;
}
