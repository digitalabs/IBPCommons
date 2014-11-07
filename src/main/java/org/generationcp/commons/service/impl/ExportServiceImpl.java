package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.service.ExportService;

public class ExportServiceImpl implements ExportService{

	@Override
	public File generateCSVFile(List<Map<Integer, ExportColumnValue>> exportColumnValues,
			List<ExportColumnHeader> exportColumnHeaders, String fileName) throws IOException {
		return new File("stub");
	}


}
