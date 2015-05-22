package org.generationcp.commons.parsing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.generationcp.commons.parsing.FileParsingException;
import org.generationcp.commons.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

public abstract class AbstractCsvFileParser<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			AbstractCsvFileParser.class);
	
	protected String originalFilename;

	protected final static String CSV_FILE_EXTENSION = "csv";
	
	@Resource
	protected FileService fileService;
	
	@Resource
	protected MessageSource messageSource;
	
	protected File csvFile;
	
	
	public T parseFile(MultipartFile file) throws FileParsingException {
		this.csvFile = storeAndRetrieveFile(file);
		
		Map<Integer, List<String>> csvMap = new HashMap<>();
		
		try{
			 CSVReader reader = new CSVReader(new FileReader(csvFile));
			 
			 String nextLine[];
			 Integer key = 0;
		     while ((nextLine = reader.readNext()) != null) {
		    	 csvMap.put(key++, Arrays.asList(nextLine));
		     }
		     
		     reader.close();
		     
		}catch(IOException e){
			throw new FileParsingException(messageSource.getMessage("common.error.invalid.file", null,Locale.ENGLISH));
		}
		
		return parseCsvMap(csvMap);
	}

	public abstract T parseCsvMap(Map<Integer, List<String>> csvMap) throws FileParsingException;

	public File storeAndRetrieveFile(MultipartFile multipartFile)
			throws FileParsingException {
		try {
			
			this.originalFilename = multipartFile.getOriginalFilename();

			if (!isFileExtensionSupported(originalFilename)) {
				throw new InvalidFormatException("Unsupported file format");
			}

			String serverFilename = fileService.saveTemporaryFile(multipartFile.getInputStream());

			return fileService.retrieveFileFromFileName(serverFilename);
			
		} catch (InvalidFormatException | IOException e) {
			LOG.debug(e.getMessage(), e);
			throw new FileParsingException(messageSource.getMessage("common.error.file.not.csv", null,Locale.ENGLISH));
		}
	}

	protected boolean isFileExtensionSupported(String filename) {
		boolean extensionCheckResult = false;

		String extension = filename.substring(filename.lastIndexOf(".") + 1,
						filename.length());

		
			if (CSV_FILE_EXTENSION.equalsIgnoreCase(extension)) {
				extensionCheckResult = true;
			}
		

		return extensionCheckResult;
	}
}
