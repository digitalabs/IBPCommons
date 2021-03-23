package org.generationcp.commons.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.middleware.pojos.workbench.PermissionsEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameGeneratorTest {

	private static final String PASSWORD = "admin2";
	private static final String USERNAME = "admin1";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hhmmss");

	@Before
	public void setup() {
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + PermissionsEnum.ADMIN.name());
		final UsernamePasswordAuthenticationToken loggedInUser =
			new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, Lists.newArrayList(roleAuthority));
		SecurityContextHolder.getContext().setAuthentication(loggedInUser);
	}

	@Test
	public void testFileNameWithExtension() {
		final String expectedFileName =
			"Original_" + USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date()) + ".xls";
		final String generateFileName = FileNameGenerator.generateFileName("Original", ".xls");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertEquals(expectedFileName, generateFileName);
	}

	@Test
	public void testFileNameWoExtension() {
		final String expectedFileName =
			"Original_" + USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date());
		final String generateFileName = FileNameGenerator.generateFileName("Original");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertFalse(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertEquals(expectedFileName, generateFileName);
	}

	@Test
	public void testFileNameAppendExtension() {
		final String expectedFileName =
			"Original_" + USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date()) + ".xls";
		final String generateFileName = FileNameGenerator.generateFileName("Original", "xls");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertEquals(expectedFileName, generateFileName);
	}

	@Test
	public void testFileNameTruncateWExtension() {
		final int maxSize = FileNameGenerator.MAX_SIZE;
		final String originalFileName = RandomStringUtils.randomAlphabetic(maxSize);
		final String tempExpectedFileName =
			originalFileName + "_"+ USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date()) + ".xls";
		final int begin = tempExpectedFileName.length() - maxSize;
		final String expectedFileName = tempExpectedFileName.substring(begin, tempExpectedFileName.length());

		final String generateFileName = FileNameGenerator.generateFileName(originalFileName, "xls");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertEquals("Truncate will start from the beginning",expectedFileName, generateFileName);
	}

	@Test
	public void testFileNameTruncateWoExtension() {
		final int maxSize = FileNameGenerator.MAX_SIZE_WO_EXTENSION;
		final String originalFileName = RandomStringUtils.randomAlphabetic(maxSize);
		final String tempExpectedFileName =
			originalFileName + "_"+ USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date());
		final int begin = tempExpectedFileName.length() - maxSize;
		final String expectedFileName = tempExpectedFileName.substring(begin, tempExpectedFileName.length());

		final String generateFileName = FileNameGenerator.generateFileName(originalFileName);
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertEquals(expectedFileName, generateFileName);
	}

	@Test
	public void testDifferentFileTypes() {
		final String userName = RandomStringUtils.randomAlphabetic(5);
		final String xlsFileName = RandomStringUtils.randomAlphabetic(10) + userName + "_20210322_080607.xls";
		final String csvFileName = RandomStringUtils.randomAlphabetic(10) + userName + "_20210322_080607.csv";
		final String xlsxFileName = RandomStringUtils.randomAlphabetic(10) + userName + "_20210322_080607.xlsx";
		final String xmlFileName = RandomStringUtils.randomAlphabetic(10) + userName + "_20210322_080607.xml";
		final String zipFileName = RandomStringUtils.randomAlphabetic(10) + userName + "_20210322_080607.zip";
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(xlsFileName, FileNameGenerator.XLS_DATE_TIME_PATTERN));
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(csvFileName, FileNameGenerator.CSV_DATE_TIME_PATTERN));
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(xlsxFileName, FileNameGenerator.XLSX_DATE_TIME_PATTERN));
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(xmlFileName, FileNameGenerator.XML_DATE_TIME_PATTERN));
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(zipFileName, FileNameGenerator.ZIP_DATE_TIME_PATTERN));
	}
}
