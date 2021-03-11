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
		final String generateFileName = FileNameGenerator.generateFileName("Original", null);
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
		final int maxSize = 255;
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
		final int maxSize = 250;
		final String originalFileName = RandomStringUtils.randomAlphabetic(maxSize);
		final String tempExpectedFileName =
			originalFileName + "_"+ USERNAME + "_" + DATE_FORMAT.format(new Date()) + "_" + TIME_FORMAT.format(new Date());
		final int begin = tempExpectedFileName.length() - maxSize;
		final String expectedFileName = tempExpectedFileName.substring(begin, tempExpectedFileName.length());

		final String generateFileName = FileNameGenerator.generateFileName(originalFileName, null);
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertEquals(expectedFileName, generateFileName);
	}
}
