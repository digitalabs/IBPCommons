
package org.generationcp.commons.parsing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.generationcp.commons.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

public abstract class AbstractCsvFileProcessor<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractCsvFileProcessor.class);

	protected String originalFilename;

	protected final static String CSV_FILE_EXTENSION = "csv";

	@Resource
	protected FileService fileService;

	@Resource
	protected MessageSource messageSource;

	protected File csvFile;

	public T parseFile(MultipartFile file) throws FileParsingException {
		this.csvFile = this.storeAndRetrieveFile(file);

		return this.parseFile(this.csvFile.getAbsolutePath());
	}

	public T parseFile(String absoluteFilename) throws FileParsingException {
		try {
			this.csvFile = new File(absoluteFilename);

			Map<Integer, List<String>> csvMap = new HashMap<>();


			CSVReader reader = new CSVReader(new FileReader(this.csvFile));
			String nextLine[];
			Integer key = 0;
			while ((nextLine = reader.readNext()) != null) {
				//add empty array for whitespace lines for empty array list checking
				if(!StringUtils.join(nextLine).trim().isEmpty()){
					csvMap.put(key++, Arrays.asList(nextLine));
				} else {
					csvMap.put(key++, new ArrayList<String>());
				}
			}

			reader.close();


			return this.parseCsvMap(csvMap);
		} catch (IOException e) {
			throw new FileParsingException(this.messageSource.getMessage("common.error.invalid.file", null, Locale.ENGLISH));
		}
	}

	public abstract T parseCsvMap(Map<Integer, List<String>> csvMap) throws FileParsingException;

	public File storeAndRetrieveFile(MultipartFile multipartFile) throws FileParsingException {
		try {

			this.originalFilename = multipartFile.getOriginalFilename();

			if (!this.isFileExtensionSupported(this.originalFilename)) {
				throw new InvalidFormatException("Unsupported file format");
			}

			String serverFilename = this.fileService.saveTemporaryFile(multipartFile.getInputStream());

			return this.fileService.retrieveFileFromFileName(serverFilename);

		} catch (InvalidFormatException | IOException e) {
			AbstractCsvFileProcessor.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage("common.error.file.not.csv", null, Locale.ENGLISH));
		}
	}

	protected boolean isFileExtensionSupported(String filename) {
		boolean extensionCheckResult = false;

		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		if (AbstractCsvFileProcessor.CSV_FILE_EXTENSION.equalsIgnoreCase(extension)) {
			extensionCheckResult = true;
		}

		return extensionCheckResult;
	}
}
