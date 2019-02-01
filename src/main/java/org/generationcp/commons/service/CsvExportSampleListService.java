package org.generationcp.commons.service;

import org.generationcp.commons.pojo.FileExportInfo;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;

import java.io.IOException;
import java.util.List;

public interface CsvExportSampleListService {

	FileExportInfo export(
		final List<SampleDetailsDTO> sampleDetailsDCsvExportSampleListServiceTOs, final String filename, final List<String> visibleColumns,
		final String enumeratorVariableName)
		throws IOException;
}
