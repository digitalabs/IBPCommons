
package org.generationcp.commons.parsing;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.service.FileService;
import org.generationcp.middleware.util.PoiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/24/2015 Time: 5:38 PM
 */
public abstract class AbstractExcelFileParser<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractExcelFileParser.class);
	protected Workbook workbook;
	protected String originalFilename;

	public static final String FILE_INVALID = "common.error.excel.invalid.format";
	public static final String FILE_READ_ERROR = "common.error.excel.read.error";

	protected static final String[] EXCEL_FILE_EXTENSIONS = new String[] {"xls", "xlsx"};

	@Resource
	protected FileService fileService;

	@Resource
	protected MessageSource messageSource;

	public T parseFile(MultipartFile file, Map<String, Object> additionalParams) throws FileParsingException {
		this.workbook = this.storeAndRetrieveWorkbook(file);
		return this.parseWorkbook(this.workbook, additionalParams);
	}

	public T parseFile(String absoluteFilename, Map<String, Object> additionalParams) throws FileParsingException {
		try {

			this.workbook = this.fileService.retrieveWorkbook(absoluteFilename);
			return this.parseWorkbook(this.workbook, additionalParams);

		} catch (InvalidFormatException e) {
			AbstractExcelFileParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage(FILE_INVALID, null, Locale.ENGLISH));
		} catch (IOException e) {
			AbstractExcelFileParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage(FILE_READ_ERROR, null, Locale.ENGLISH));
		}

	}

	public String[] getSupportedFileExtensions() {
		return AbstractExcelFileParser.EXCEL_FILE_EXTENSIONS;
	}

	public abstract T parseWorkbook(Workbook workbook, Map<String, Object> additionalParams) throws FileParsingException;

	public Workbook storeAndRetrieveWorkbook(MultipartFile multipartFile) throws FileParsingException {
		try {
			this.originalFilename = multipartFile.getOriginalFilename();

			if (!this.isFileExtensionSupported(this.originalFilename)) {
				throw new InvalidFormatException("Unsupported file format");
			}

			String serverFilename = this.fileService.saveTemporaryFile(multipartFile.getInputStream());

			return this.fileService.retrieveWorkbook(serverFilename);
		} catch (InvalidFormatException e) {
			AbstractExcelFileParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage(FILE_INVALID, null, Locale.ENGLISH));
		} catch (IOException e) {
			AbstractExcelFileParser.LOG.debug(e.getMessage(), e);
			throw new FileParsingException(this.messageSource.getMessage(FILE_READ_ERROR, null, Locale.ENGLISH));
		}
	}

	protected boolean isFileExtensionSupported(final String filename) {
		boolean extensionCheckResult = false;

		final String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		final String[] supportedExtensions = this.getSupportedFileExtensions();
		assert supportedExtensions != null && supportedExtensions.length > 0;

		for (final String supported : supportedExtensions) {
			if (supported.equalsIgnoreCase(extension)) {
				extensionCheckResult = true;
				break;
			}
		}

		return extensionCheckResult;
	}

	protected boolean isHeaderInvalid(final int headerNo, final int sheetNumber, final String[] headers) {
		boolean isInvalid = false;

		for (int i = 0; i < headers.length; i++) {
			isInvalid = isInvalid || !headers[i].equalsIgnoreCase(this.getCellStringValue(sheetNumber, headerNo, i));
		}

		return isInvalid;
	}

	/**
	 * Wrapper to PoiUtil.getCellStringValue static call so we can stub the methods on unit tests
	 * 
	 * @param sheetNo number of excel sheet
	 * @param rowNo number of excel row
	 * @param columnNo number of excel column
	 * @return cell string value
	 */
	public String getCellStringValue(final int sheetNo, final int rowNo, final Integer columnNo) {
		final String out = null == columnNo ? "" : PoiUtil.getCellStringValue(this.workbook, sheetNo, rowNo, columnNo);
		return null == out ? "" : out;
	}

	/**
	 * Wrapper to PoiUtil.getCellNumericValue static call so we can stub the methods on unit tests
	 *
	 * @param sheetNo number of excel sheet
	 * @param rowNo number of excel row
	 * @param columnNo number of excel column
	 * @return cell numeric value
	 */
	public Double getCellNumericValue(final int sheetNo, final int rowNo, final Integer columnNo) {
		return PoiUtil.getCellNumericValue(this.workbook, sheetNo, rowNo, columnNo);
	}

	/**
	 * Wrapper to PoiUtil.rowIsEmpty static call so we can stub the methods on unit tests
	 * 
	 * @param sheetNo number of excel sheet
	 * @param rowNo number of excel row
	 * @param colCount
	 * @return true if row is empty
	 */
	public boolean isRowEmpty(final int sheetNo, final int rowNo, final int colCount) {
		return PoiUtil.rowIsEmpty(this.workbook.getSheetAt(sheetNo), rowNo, 0, colCount - 1);
	}

}
