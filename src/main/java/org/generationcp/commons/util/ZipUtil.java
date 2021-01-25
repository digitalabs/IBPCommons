
package org.generationcp.commons.util;

import com.google.common.io.Files;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static final String ZIP_EXTENSION = ".zip";
	private static final int BUFFER_SIZE = 4096;
	private static final Logger LOG = LoggerFactory.getLogger(ZipUtil.class);

	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public File zipFiles(final String fileName, final List<File> files)
		throws IOException {
		final byte[] buffer = new byte[1024];

		final File temporaryFolder = Files.createTempDir();
		final String sanitizedFileName = FileUtils.sanitizeFileName(ExportFileName.getInstance().generateFileName(fileName, ZIP_EXTENSION));
		final String zipPath = temporaryFolder.getAbsolutePath() + "/" + sanitizedFileName;
		final FileOutputStream fos = new FileOutputStream(zipPath);

		final ZipOutputStream zos = new ZipOutputStream(fos);

		try {

			for (final File file : files) {

				final ZipEntry ze = new ZipEntry(file.getName());
				zos.putNextEntry(ze);

				try (final FileInputStream in = new FileInputStream(file)) {
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				}
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
		final byte[] buffer = new byte[1024];

		final String fileName = ExportFileName.getInstance().generateFileName(fileNameWithoutExtension, ZIP_EXTENSION, false);
		final String fileNameUnderWorkspaceDirectory = this.installationDirectoryUtil
			.getTempFileInOutputDirectoryForProjectAndTool(fileName, ZIP_EXTENSION, project, tool);
		final FileOutputStream fos = new FileOutputStream(fileNameUnderWorkspaceDirectory);
		try (final ZipOutputStream zos = new ZipOutputStream(fos)) {

			for (final String file : filenameList) {

				final File f = new File(file);
				final ZipEntry ze = new ZipEntry(f.getName());
				zos.putNextEntry(ze);

				try (final FileInputStream in = new FileInputStream(file)) {

					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				}
			}

			zos.closeEntry();
		}

		return fileNameUnderWorkspaceDirectory;
	}

	public void extractZip(final String zipFile, final String destination) {

		final File file = new File(zipFile);
		final InputStream input;

		try {

			input = new FileInputStream(file);
			try (final ZipInputStream zip = new ZipInputStream(input)) {
				ZipEntry entry = zip.getNextEntry();

				while (entry != null) {

					final String filePath = destination + File.separator + entry.getName();

					this.extractFile(zip, filePath);

					entry = zip.getNextEntry();
				}
			}
			input.close();
		} catch (final IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

	}

	public File extractZipSpecificFile(final String zipFile, final String fileNameToExtract, final Project project, final ToolName tool) {

		File extractedFile = null;
		final File file = new File(zipFile);
		final InputStream input;

		try {

			input = new FileInputStream(file);
			try (final ZipInputStream zip = new ZipInputStream(input)) {
				ZipEntry entry = zip.getNextEntry();

				while (entry != null) {

					if (entry.getName().toLowerCase().contains(fileNameToExtract.toLowerCase())) {
						final String fileNameUnderWorkspaceDirectory =
							this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(project, tool);
						final String filePath = fileNameUnderWorkspaceDirectory + File.separator + entry.getName();
						this.extractFile(zip, filePath);
						extractedFile = new File(filePath);
						break;
					}
					entry = zip.getNextEntry();
				}
			}
			input.close();

			if (extractedFile != null) {
				return extractedFile;
			}
		} catch (final IOException e) {
			ZipUtil.LOG.error(e.getMessage(), e);
		}

		return null;

	}

	private void extractFile(final ZipInputStream zipIn, final String filePath) throws IOException {
		try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
			final byte[] bytesIn = new byte[ZipUtil.BUFFER_SIZE];
			int read = 0;
			while ((read = zipIn.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
		}
	}
}
