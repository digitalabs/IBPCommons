package org.generationcp.commons.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportColumnValue;
import org.generationcp.commons.pojo.FileExportInfo;
import org.generationcp.commons.service.CsvExportSampleListService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.FileUtils;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVWriter;

@Service
@Transactional
public class CsvExportSampleListServiceImpl implements CsvExportSampleListService {

	private static final String FILE_EXTENSION = ".csv";
	private static final Logger LOG = LoggerFactory.getLogger(CsvExportSampleListServiceImpl.class);

	public static final String SAMPLE_ENTRY = "SAMPLE_ENTRY";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String PLOT_NO = "PLOT_NO";
	public static final String PLANT_NO = "PLANT_NO";
	public static final String SAMPLE_NAME = "SAMPLE_NAME";
	public static final String TAKEN_BY = "TAKEN_BY";
	public static final String SAMPLING_DATE = "SAMPLING_DATE";
	public static final String SAMPLE_UID = "SAMPLE_UID";
	public static final String PLANT_UID = "PLANT_UID";
	public static final String PLOT_ID = "PLOT_ID";
	public static final String GID = "GID";

	public static final List<String> AVAILABLE_COLUMNS = Collections.unmodifiableList(
			Arrays.asList(SAMPLE_ENTRY, DESIGNATION, PLOT_NO, PLANT_NO, SAMPLE_NAME, TAKEN_BY, SAMPLING_DATE, SAMPLE_UID, PLANT_UID,
					PLOT_ID, GID));

	@Resource
	private ContextUtil contextUtil;

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	@Override
	public FileExportInfo export(final List<SampleDetailsDTO> sampleDetailsDTOs, final String filenameWithoutExtension,
			final List<String> visibleColumns) throws IOException {
		LOG.debug("Initialize export");

		final List<ExportColumnHeader> exportColumnHeaders = this.getExportColumnHeaders(visibleColumns);
		final List<Map<Integer, ExportColumnValue>> exportColumnValues = this.getExportColumnValues(exportColumnHeaders, sampleDetailsDTOs);

		final String cleanFilenameWithoutExtension = FileUtils.sanitizeFileName(filenameWithoutExtension);
		final String filenamePath = this.installationDirectoryUtil
				.getTempFileInOutputDirectoryForProjectAndTool(cleanFilenameWithoutExtension, FILE_EXTENSION,
						this.contextUtil.getProjectInContext(), ToolName.FIELDBOOK_WEB);
		this.generateCSVFile(exportColumnValues, exportColumnHeaders, filenamePath);

		LOG.debug("Finished export");

		return new FileExportInfo(filenamePath, cleanFilenameWithoutExtension + FILE_EXTENSION);
	}

	protected List<Map<Integer, ExportColumnValue>> getExportColumnValues(List<ExportColumnHeader> columnHeaders,
			List<SampleDetailsDTO> sampleDetailsDTOs) {
		final List<Map<Integer, ExportColumnValue>> exportColumnValues = new ArrayList<>();
		int i = 1;
		for (final SampleDetailsDTO sampleDetailsDTO : sampleDetailsDTOs) {
			sampleDetailsDTO.setSampleEntryNo(i++);
			exportColumnValues.add(this.getColumnValueMap(columnHeaders, sampleDetailsDTO));
		}

		return exportColumnValues;

	}

	protected List<ExportColumnHeader> getExportColumnHeaders(List<String> visibleColumns) {
		final List<ExportColumnHeader> exportColumnHeaders = new ArrayList<>();

		int i = 0;
		if (!visibleColumns.contains(SAMPLE_UID)) {
			visibleColumns.add(SAMPLE_UID);
		}
		for (final String column : AVAILABLE_COLUMNS) {
			if (visibleColumns.contains(column)) {
				exportColumnHeaders.add(new ExportColumnHeader(i++, column, true));

			}
		}

		return exportColumnHeaders;
	}

	private Map<Integer, ExportColumnValue> getColumnValueMap(final List<ExportColumnHeader> columns,
			final SampleDetailsDTO sampleDetailsDTO) {
		final Map<Integer, ExportColumnValue> columnValueMap = new HashMap<>();

		for (final ExportColumnHeader column : columns) {
			final Integer id = column.getId();
			columnValueMap.put(id, this.getColumnValue(sampleDetailsDTO, column));
		}

		return columnValueMap;
	}

	private ExportColumnValue getColumnValue(final SampleDetailsDTO sampleDetailsDTO, final ExportColumnHeader column) {
		ExportColumnValue columnValue = null;

		switch (column.getName()) {
			case SAMPLE_ENTRY:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getSampleEntryNo().toString());
				break;
			case DESIGNATION:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getDesignation());
				break;
			case PLOT_NO:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getPlotNumber());
				break;
			case PLANT_NO:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getPlantNo().toString());
				break;
			case SAMPLE_NAME:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getSampleName());
				break;
			case TAKEN_BY:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getTakenBy());
				break;
			case SAMPLING_DATE:
				columnValue = setSampleDateVale(column.getId(), sampleDetailsDTO.getSampleDate());
				break;
			case SAMPLE_UID:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getSampleBusinessKey());
				break;
			case PLANT_UID:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getPlantBusinessKey());
				break;
			case PLOT_ID:
				columnValue = new ExportColumnValue(column.getId(), sampleDetailsDTO.getPlotId());
				break;
			case GID:
				columnValue = new ExportColumnValue(column.getId(), String.valueOf(sampleDetailsDTO.getGid()));
				break;
			default:
				break;
		}
		return columnValue;
	}

	private ExportColumnValue setSampleDateVale(final Integer id, final Date sampleDate) {
		if (null != sampleDate) {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			final String convertedCurrentDate = sdf.format(sampleDate);
			return new ExportColumnValue(id, convertedCurrentDate);
		} else {
			return new ExportColumnValue(id, "-");
		}
	}

	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath) throws IOException {
		return this.generateCSVFile(exportColumnValues, exportColumnHeaders, fileNameFullPath, true);
	}

	public File generateCSVFile(final List<Map<Integer, ExportColumnValue>> exportColumnValues,
			final List<ExportColumnHeader> exportColumnHeaders, final String fileNameFullPath, final boolean includeHeader)
			throws IOException {
		final File newFile = new File(fileNameFullPath);

		final CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(fileNameFullPath), "UTF-8"), ',');

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

	private String[] getColumnValues(final Map<Integer, ExportColumnValue> exportColumnMap,
			final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<>();
		for (final ExportColumnHeader exportColumnHeader : exportColumnHeaders) {
			if (exportColumnHeader.isDisplay()) {
				final ExportColumnValue exportColumnValue = exportColumnMap.get(exportColumnHeader.getId());
				String colName = "";
				if (exportColumnValue != null) {
					colName = exportColumnValue.getValue();
				}
				values.add(colName);
			}
		}
		return values.toArray(new String[values.size()]);
	}

	private String[] getColumnHeaderNames(final List<ExportColumnHeader> exportColumnHeaders) {
		final List<String> values = new ArrayList<>();
		for (final ExportColumnHeader exportColumnHeader : exportColumnHeaders) {
			if (exportColumnHeader.isDisplay()) {
				values.add(exportColumnHeader.getName());
			}
		}
		return values.toArray(new String[values.size()]);
	}

	public void setContextUtil(ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}