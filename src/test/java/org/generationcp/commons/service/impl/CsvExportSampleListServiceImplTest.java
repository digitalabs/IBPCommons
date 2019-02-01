package org.generationcp.commons.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportRow;
import org.generationcp.commons.pojo.FileExportInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.util.SampleListUtilTest;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class CsvExportSampleListServiceImplTest {
	
	private static final String FILENAME = "Study 33-Sample List Name";

	@Mock
	private ContextUtil contextUtil;

	private static final String CSV_EXT = ".csv";

	private CsvExportSampleListServiceImpl csvExportSampleListService;

	private Random random = new Random();


	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		// Need to spy so writing of actual CSV file won't be performed
		this.csvExportSampleListService = Mockito.spy(new CsvExportSampleListServiceImpl());
		this.csvExportSampleListService.setContextUtil(this.contextUtil);
		Mockito.doReturn(Mockito.mock(File.class)).when(this.csvExportSampleListService)
			.generateCSVFile(Matchers.any(List.class), Matchers.any(List.class), Matchers.anyString());
		
		Mockito.doReturn(ProjectTestDataInitializer.createProject()).when(this.contextUtil).getProjectInContext();
	}

	@Test
	public void testCSVSampleListExport() throws IOException {
		final List<SampleDetailsDTO> sampleDetailsDTOs = SampleListUtilTest.initSampleDetailsDTOs();
		final List<String> visibleColumns = SampleListUtilTest.getVisibleColumns();
		final FileExportInfo exportInfo =
				this.csvExportSampleListService.export(sampleDetailsDTOs, CsvExportSampleListServiceImplTest.FILENAME, visibleColumns);
		assertThat(CsvExportSampleListServiceImplTest.FILENAME + CSV_EXT, equalTo(exportInfo.getDownloadFileName()));
		final File outputFilePath = this.getOutputFilePath();
		assertThat(outputFilePath.getAbsolutePath(), equalTo(exportInfo.getFilePath()));
	}

	@Test
	public void testGetExportColumnHeaders() {

		final List<ExportColumnHeader> exportColumnHeaders =
				this.csvExportSampleListService.getExportColumnHeaders(CsvExportSampleListServiceImpl.AVAILABLE_COLUMNS);

		Assert.assertEquals(exportColumnHeaders.size(), CsvExportSampleListServiceImpl.AVAILABLE_COLUMNS.size());

		final Map<String, ExportColumnHeader> map = Maps.uniqueIndex(exportColumnHeaders, new Function<ExportColumnHeader, String>() {

			@Nullable
			@Override
			public String apply(@Nullable final ExportColumnHeader exportColumnHeader) {
				return exportColumnHeader.getName();
			}
		});

		int counter = 0;
		for (final String columnName : CsvExportSampleListServiceImpl.AVAILABLE_COLUMNS) {
			final ExportColumnHeader exportColumnHeader = map.get(columnName);
			Assert.assertEquals(exportColumnHeader.getId().intValue(), counter);
			Assert.assertEquals(exportColumnHeader.getName(), columnName);
			Assert.assertTrue(exportColumnHeader.isDisplay());
			counter++;
		}

	}

	@Test
	public void testGetExportColumnValues() {

		final List<ExportColumnHeader> exportColumnHeaders =
				this.csvExportSampleListService.getExportColumnHeaders(CsvExportSampleListServiceImpl.AVAILABLE_COLUMNS);
		final List<SampleDetailsDTO> sampleDetailsDTOS = this.createSampleDetailsDTOS(2);

		final List<ExportRow> exportRows =
				this.csvExportSampleListService.getExportColumnValues(exportColumnHeaders, sampleDetailsDTOS);

		for (int i = 0; i < sampleDetailsDTOS.size(); i++) {

			final ExportRow row = exportRows.get(i);
			for (ExportColumnHeader columnHeader : exportColumnHeaders) {
				verifyExportColumnValue(columnHeader, sampleDetailsDTOS.get(i), row.getValueForColumn(columnHeader.getId()));
			}
		}

	}

	private void verifyExportColumnValue(final ExportColumnHeader header, final SampleDetailsDTO sampleDetailDTO,
			final String value) {

		switch (header.getName()) {
			case CsvExportSampleListServiceImpl.SAMPLE_ENTRY:
				Assert.assertEquals(value, sampleDetailDTO.getSampleEntryNo().toString());
				break;
			case CsvExportSampleListServiceImpl.DESIGNATION:
				Assert.assertEquals(value, sampleDetailDTO.getDesignation());
				break;
			case CsvExportSampleListServiceImpl.PLOT_NO:
				Assert.assertEquals(value, sampleDetailDTO.getPlotNumber());
				break;
			case CsvExportSampleListServiceImpl.SAMPLE_NAME:
				Assert.assertEquals(value, sampleDetailDTO.getSampleName());
				break;
			case CsvExportSampleListServiceImpl.TAKEN_BY:
				Assert.assertEquals(value, sampleDetailDTO.getTakenBy());
				break;
			case CsvExportSampleListServiceImpl.SAMPLING_DATE:
				Assert.assertEquals(new Date("01/01/2017"), sampleDetailDTO.getSampleDate());
				break;
			case CsvExportSampleListServiceImpl.SAMPLE_UID:
				Assert.assertEquals(value, sampleDetailDTO.getSampleBusinessKey());
				break;
			case CsvExportSampleListServiceImpl.OBS_UNIT_ID:
				Assert.assertEquals(value, sampleDetailDTO.getObsUnitId());
				break;
			case CsvExportSampleListServiceImpl.GID:
				Assert.assertEquals(value, String.valueOf(sampleDetailDTO.getGid()));
				break;
			case CsvExportSampleListServiceImpl.SAMPLE_NO:
				Assert.assertEquals(value, String.valueOf(sampleDetailDTO.getSampleNumber()));
				break;
			case CsvExportSampleListServiceImpl.PLANT_NO:
			case CsvExportSampleListServiceImpl.QUADRAT_NO:
			case CsvExportSampleListServiceImpl.DATE_NO:
				Assert.assertEquals(value, String.valueOf(sampleDetailDTO.getObservationUnitNumber()));
				break;
			default:
				break;
		}

	}

	private List<SampleDetailsDTO> createSampleDetailsDTOS(final int numberOfItems) {

		final List<SampleDetailsDTO> sampleDetailsDTOS = new ArrayList<>();

		for (int i = 0; i < numberOfItems; i++) {

			final SampleDetailsDTO item = new SampleDetailsDTO();
			item.setDesignation("Designation " + i);
			item.setPlotNumber(String.valueOf(i));
			item.setSampleName("Sample Name " + i);
			item.setTakenBy("John Doe");
			item.setSampleDate(new Date("01/01/2017"));
			item.setSampleBusinessKey("SampleBusinessKeyId" + i);
			item.setObsUnitId(String.valueOf(i));
			item.setGid(i);
			item.setSampleNumber(random.nextInt(100));
			item.setObservationUnitNumber(random.nextInt(100));
			sampleDetailsDTOS.add(item);

		}

		return sampleDetailsDTOS;

	}

	private File getOutputFilePath() {
		final String outputDirectoryPath = this.installationDirectoryUtil
				.getOutputDirectoryForProjectAndTool(this.contextUtil.getProjectInContext(), ToolName.FIELDBOOK_WEB);
		final File outputDirectoryFile = new File(outputDirectoryPath);
		Assert.assertTrue(outputDirectoryFile.exists());
		File outputFile = null;
		for (final File file : outputDirectoryFile.listFiles()) {
			if (file.getName().startsWith(CsvExportSampleListServiceImplTest.FILENAME)) {
				outputFile = file;
			}
		}
		return outputFile;
	}
	
	@After
	public void cleanup() {
		this.deleteTestInstallationDirectory();
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}
}
