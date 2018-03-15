package org.generationcp.commons.util;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtilTest {

	private static final String ZIP_TO_EXTRACT_ZIP = "zipToExtract.zip";
	private static final Logger LOG = LoggerFactory.getLogger(ZipUtilTest.class);
	private List<String> filenameList;
	private static final String ZIP_FILENAME_WITHOUT_EXTENSION = "test";

	private ZipUtil zipUtil = new ZipUtil();
	private Project project;
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	

	@Before
	public void setUp() {
		this.filenameList = new ArrayList<String>();
		this.filenameList.add("test1.txt");
		this.filenameList.add("test2.txt");
		try {
			for (String fName : this.filenameList) {
				File f = new File(fName);

				f.createNewFile();

			}
		} catch (IOException e) {
			ZipUtilTest.LOG.error(e.getMessage(), e);
		}
		this.project = ProjectTestDataInitializer.createProject();
	}

	private void deleteFiles() {
		for (String fName : this.filenameList) {
			File f = new File(fName);
			f.delete();
		}
		this.deleteTestInstallationDirectory();
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}

	/**
	 * Test file zipping.
	 * @throws IOException 
	 */
	@Test
	public void testFileZipping() throws IOException {
		
		final String zipFilePath = this.zipUtil.zipIt(ZIP_FILENAME_WITHOUT_EXTENSION, this.filenameList, this.project, ToolName.BV_SSA);
		final String outputDirectory = this.installationDirectoryUtil.getOutputDirectoryForProjectAndTool(this.project, ToolName.BV_SSA);
		Assert.assertTrue(zipFilePath.contains(outputDirectory));
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			int size = 0;
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				Debug.println(0, entry.getName());
				Assert.assertFalse(!this.filenameList.contains(entry.getName()));
				size++;
			}
			Assert.assertEquals(this.filenameList.size(), size);
			zipFile.close();
		} catch (IOException e) {
			ZipUtilTest.LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * Test file extraction.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testFileExtraction() throws URISyntaxException {

		File file = new File(ClassLoader.getSystemClassLoader().getResource(ZIP_TO_EXTRACT_ZIP).toURI());
		String destination = ClassLoader.getSystemResource("").getPath();
		zipUtil.extractZip(file.getAbsolutePath(), destination);

		File testFile = new File(destination + File.separator + "test.txt");
		Assert.assertTrue(testFile.exists());

		testFile.delete();
	}

	/**
	 * Test file extraction - specific file.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testFileExtractionSpecificFile() throws URISyntaxException {
		// Create input directory where the file will be extracted to
		final String inputDirectoryPath = this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(this.project, ToolName.BV_SSA);
		final File inputDirectoryFile = new File(inputDirectoryPath);
		if (!inputDirectoryFile.exists()) {
			inputDirectoryFile.mkdirs();
		}
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource(ZIP_TO_EXTRACT_ZIP).toURI());
		File extractedFile = zipUtil.extractZipSpecificFile(file.getAbsolutePath(), "test.txt", this.project, ToolName.BV_SSA);
		Assert.assertNotNull(extractedFile);
		Assert.assertTrue(extractedFile.exists());
		// Check that extracted file is in input directory of project and tool
		Assert.assertEquals(inputDirectoryFile, extractedFile.getParentFile());
		
	}
	
	@After
	public void cleanup() {
		this.deleteFiles();
	}
}

