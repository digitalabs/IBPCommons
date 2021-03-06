package org.generationcp.commons.util;

import org.generationcp.middleware.domain.sample.SampleDetailsDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class SampleListUtilTest {

	public static List<SampleDetailsDTO> initSampleDetailsDTOs() {
		final List<SampleDetailsDTO> sampleDetailsDTOs = new ArrayList<>();
		final SampleDetailsDTO sampleDetailsDTO = new SampleDetailsDTO(1, "ADFETYESDADF", "saDSAFHGRHSRT");
		sampleDetailsDTO.setDesignation("AAB");
		sampleDetailsDTO.setSampleName("SAMPLE1");
		sampleDetailsDTO.setSampleDate(new Date());
		sampleDetailsDTO.setObservationUnitNumber("1");
		sampleDetailsDTOs.add(sampleDetailsDTO);

		return sampleDetailsDTOs;
	}

	public static  List<String> getVisibleColumns() {
		final List<String> visibleColumns = new ArrayList<>();
		visibleColumns.add("SAMPLE_ENTRY");
		visibleColumns.add("DESIGNATION");
		visibleColumns.add("PLOT_NO");
		visibleColumns.add("SAMPLE_NAME");
		visibleColumns.add("TAKEN_BY");
		visibleColumns.add("SAMPLING_DATE");

		return visibleColumns;
	}

	public static String getColumnsExport() {
		return "SAMPLE_ENTRY,DESIGNATION,PLOT_NO,SAMPLE_NAME,TAKEN_BY,SAMPLING_DATE";
	}

}
