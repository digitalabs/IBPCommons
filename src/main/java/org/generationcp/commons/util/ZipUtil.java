
package org.generationcp.commons.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtil {

	private static final String TEMP_FILE_DIR = new File(System.getProperty("java.io.tmpdir")).getPath();
	public static final String ZIP_EXTENSION = ".zip";
	private static final int BUFFER_SIZE = 4096;
	private static final Logger LOG = LoggerFactory.getLogger(ZipUtil.class);
	
	final private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public File zipFiles(final List<File> files)
		throws IOException {
		final byte[] buffer = new byte[1024];

		final String zipPath = TEMP_FILE_DIR + UUID.randomUUID() + ZIP_EXTENSION;
		FileOutputStream fos = new FileOutputStream(zipPath);

		final ZipOutputStream zos = new ZipOutputStream(fos);

		try {

			for (File file : files) {

				ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}
			zos.closeEntry();

		} finally {
			// remember close it
			zos.close();
		}

		return new File(zipPath);
	}

	public String zipIt(final String fileNameWithoutExtension, final List<String> filenameList, final Project project, final ToolName tool)
			throws IOException {
		byte[] buffer = new byte[1024];

		final String fileNameUnderWorkspaceDirectory = this.installationDirectoryUtil
				.getTempFileInOutputDirectoryForProjectAndTool(fileNameWithoutExtension, ZIP_EXTENSION, project, tool);
		FileOutputStream fos = new FileOutputStream(fileNameUnderWorkspaceDirectory);
		ZipOutputStream zos = new ZipOutputStream(fos);

		for (String file : filenameList) {

			File f = new File(file);
			ZipEntry ze = new ZipEntry(f.getName());
			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(file);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
		}

		zos.closeEntry();
		// remember close it
		zos.close();

		return fileNameUnderWorkspaceDirectory;
	}
	
	public void extractZip(String zipFile, String destination) {

		File file = new File(zipFile);
		InputStream input;

		try {

			input = new FileInputStream(file);
			ZipInputStream zip = new ZipInputStream(input);
			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {

				String filePath = destination + File.separator + entry.getName();

				extractFile(zip, filePath);

				entry = zip.getNextEntry();
			}

			zip.close();
			input.close();

		} catch (FileNotFoundException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

	}

	public File extractZipSpecificFile(final String zipFile, final String fileNameToExtract, final Project project, final ToolName tool) {

		File extractedFile = null;
		File file = new File(zipFile);
		InputStream input;

		try {

			input = new FileInputStream(file);
			ZipInputStream zip = new ZipInputStream(input);
			ZipEntry entry = zip.getNextEntry();

			while (entry != null) {

				if (entry.getName().toLowerCase().contains(fileNameToExtract.toLowerCase())) {
					final String fileNameUnderWorkspaceDirectory =
							this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(project, tool);
					String filePath = fileNameUnderWorkspaceDirectory + File.separator + entry.getName();
					this.extractFile(zip, filePath);
					extractedFile = new File(filePath);
					break;
				}
				entry = zip.getNextEntry();
			}

			zip.close();
			input.close();

			if (extractedFile != null) {
				return extractedFile;
			}

		} catch (FileNotFoundException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

		return null;

	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[ZipUtil.BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
