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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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
		final String generateFileName = FileNameGenerator.generateFileName("Original.xls");
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
		Assert.assertTrue(generateFileName.split("_").length >= 3);
		Assert.assertTrue(FileNameGenerator.hasDate(generateFileName).isPresent());
		Assert.assertTrue(FileNameGenerator.hasTimeStamp(generateFileName).isPresent());
		Assert.assertTrue(hasUserName(generateFileName).isPresent());
		Assert.assertFalse(generateFileName.contains("."));
	}

	@Test
	public void testFileNameAppendExtension() {
		final String generateFileName = FileNameGenerator.generateFileName("Original.xls");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertTrue(FileNameGenerator.hasDate(generateFileName).isPresent());
		Assert.assertTrue(FileNameGenerator.hasTimeStamp(generateFileName).isPresent());
		Assert.assertTrue(hasUserName(generateFileName).isPresent());
	}

	@Test
	public void testFileNameTruncateWExtension() {
		final int maxSize = 256;
		final String originalFileName = RandomStringUtils.randomAlphabetic(maxSize);
		final String generateFileName = FileNameGenerator.generateFileName(originalFileName + ".xls");
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(underscores[underscores.length - 1].contains(".xls"));
		Assert.assertEquals("Truncate will start from the beginning", 204, generateFileName.length());
		Assert.assertTrue(FileNameGenerator.hasDate(generateFileName).isPresent());
		Assert.assertTrue(FileNameGenerator.hasTimeStamp(generateFileName).isPresent());
		Assert.assertTrue(hasUserName(generateFileName).isPresent());
	}

	@Test
	public void testFileNameTruncateWoExtension() {
		final int maxSize = 200;
		final String originalFileName = RandomStringUtils.randomAlphabetic(maxSize);
		final String generateFileName = FileNameGenerator.generateFileName(originalFileName);
		final String[] underscores = generateFileName.split("_");
		Assert.assertTrue(underscores.length >= 3);
		Assert.assertTrue(FileNameGenerator.hasDate(generateFileName).isPresent());
		Assert.assertTrue(FileNameGenerator.hasTimeStamp(generateFileName).isPresent());
		Assert.assertTrue(hasUserName(generateFileName).isPresent());
		Assert.assertEquals("Truncate will start from the beginning",maxSize, generateFileName.length());

	}

	private static Optional<String> hasUserName(final String fileName) {
		final String username = SecurityUtil.getLoggedInUserName();
		if (fileName.contains("_" + username)) {
			return Optional.of(username);
		}
		return Optional.empty();
	}
}
