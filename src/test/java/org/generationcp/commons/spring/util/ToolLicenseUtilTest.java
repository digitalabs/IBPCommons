
package org.generationcp.commons.spring.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.data.initializer.ToolLicenseInfoInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ToolLicenseUtilTest {

	private static final ToolLicenseUtil toolLicenseUtil = ToolLicenseUtil.getInstance();

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	private File bvLicenseFile;

	private final ToolLicenseInfoInitializer toolLicenseInfoInitializer = new ToolLicenseInfoInitializer();

	// This should be 31-mar-2016 based on the bv license file in the resources
	private Date sampleBVLicenseExpirationDate;

	@Before
	public void setUp() throws URISyntaxException, IOException {
		ToolLicenseUtilTest.toolLicenseUtil.setWorkbenchDataManager(this.workbenchDataManager);
		ToolLicenseUtilTest.toolLicenseUtil.setWorkbenchInstallationDirectory(ToolLicenseInfoInitializer.WB_INSTALLATION_DIR);
		final File sampleBVLicenseFile =
				new File(ClassLoader.getSystemClassLoader().getResource(ToolLicenseInfoInitializer.BV_LICENSE_FILENAME).toURI());
		this.bvLicenseFile = this.copyBvLicenseFileInsideWBInstallationDir(sampleBVLicenseFile);
		this.sampleBVLicenseExpirationDate = this.createBVLicenseExpirationDate();
	}
	
	@After
	public void cleanup() {
		this.bvLicenseFile.delete();
	}

	private Date createBVLicenseExpirationDate() {
		final Calendar calendar = DateUtil.getCalendarInstance();
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.YEAR, 2016);
		final Date expirationDate = calendar.getTime();
		// return date with zero time
		return DateUtils.truncate(expirationDate, java.util.Calendar.DAY_OF_MONTH);
	}

	private File copyBvLicenseFileInsideWBInstallationDir(final File sampleBVLicenseFile) throws IOException {
		final File licenseFile =
				new File(
						this.toolLicenseInfoInitializer
								.getLicensePathFromWBInstallationDir(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME));
		if (!licenseFile.getParentFile().exists()) {
			licenseFile.getParentFile().mkdirs();
		}
		Files.copy(sampleBVLicenseFile.toPath(), licenseFile.toPath(), REPLACE_EXISTING);
		return licenseFile;
	}

	@Test
	public void testLoadToolLicenseCache() {
		// gather test data
		final ToolLicenseInfo expectedToolLicenseInfo =
				this.toolLicenseInfoInitializer.createToolLicenseInfo(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		expectedToolLicenseInfo.setLicenseInfoId(1);
		final String outdatedLicensePath = expectedToolLicenseInfo.getLicensePath();
		final String outdatedLicenseHash = expectedToolLicenseInfo.getLicenseHash();
		final Date outdatedExpirationDate = expectedToolLicenseInfo.getExpirationDate();

		// mock methods
		this.mockListOfToolLicenseInfo(expectedToolLicenseInfo);
		this.mockSaveOrUpdateToolLicenseInfo(expectedToolLicenseInfo);

		// call method to test
		ToolLicenseUtilTest.toolLicenseUtil.loadToolLicenseCache();

		// verify
		final ToolLicenseInfo actualToolLicenseInfo =
				ToolLicenseUtil.toolLicenseCache.get(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		this.assertExpectedData(expectedToolLicenseInfo, actualToolLicenseInfo, outdatedLicensePath, outdatedLicenseHash,
				outdatedExpirationDate);
	}

	private void assertExpectedData(final ToolLicenseInfo expectedToolLicenseInfo, final ToolLicenseInfo actualToolLicenseInfo,
			final String outdatedLicensePath, final String outdatedLicenseHash, final Date outdatedExpirationDate) {

		Assert.assertNotNull("Breeding view license should be found.", actualToolLicenseInfo);

		Assert.assertEquals("The license info id should be equal", expectedToolLicenseInfo.getLicenseInfoId(),
				actualToolLicenseInfo.getLicenseInfoId());
		Assert.assertEquals("The tool should be equal", expectedToolLicenseInfo.getTool(), actualToolLicenseInfo.getTool());
		Assert.assertEquals("The tool name should be " + ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME,
				ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME, actualToolLicenseInfo.getTool().getToolName());

		Assert.assertEquals("The license path should be equal.", expectedToolLicenseInfo.getLicensePath(),
				actualToolLicenseInfo.getLicensePath());
		Assert.assertFalse("The license path should not be equal.", outdatedLicensePath.equals(actualToolLicenseInfo.getLicensePath()));
		final String actualLicensePath =
				this.toolLicenseInfoInitializer.getLicensePathFromWBInstallationDir(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		Assert.assertEquals("The license path should be " + actualLicensePath, actualLicensePath, actualToolLicenseInfo.getLicensePath());

		Assert.assertEquals("The license hash should be equal.", expectedToolLicenseInfo.getLicenseHash(),
				actualToolLicenseInfo.getLicenseHash());
		Assert.assertFalse("The license hash should not be equal.", outdatedLicenseHash.equals(actualToolLicenseInfo.getLicenseHash()));
		final String actualLicenseHash = ToolLicenseUtilTest.toolLicenseUtil.getLicenseHash(actualLicensePath);
		Assert.assertEquals("The license hash should be " + actualLicenseHash, actualLicenseHash, actualToolLicenseInfo.getLicenseHash());

		Assert.assertEquals("The expiration date should be equal.", expectedToolLicenseInfo.getExpirationDate(),
				actualToolLicenseInfo.getExpirationDate());
		Assert.assertFalse("The expiration date should not be equal.",
				outdatedExpirationDate.equals(actualToolLicenseInfo.getExpirationDate()));
		Assert.assertEquals("The expiration date should be " + this.sampleBVLicenseExpirationDate, this.sampleBVLicenseExpirationDate,
				actualToolLicenseInfo.getExpirationDate());
	}

	private void mockListOfToolLicenseInfo(final ToolLicenseInfo toolLicenseInfo) {
		final List<ToolLicenseInfo> listOfToolLicenseInfo = new ArrayList<>();
		listOfToolLicenseInfo.add(toolLicenseInfo);
		Mockito.doReturn(listOfToolLicenseInfo).when(this.workbenchDataManager).getListOfToolLicenseInfo();
	}

	private void mockSaveOrUpdateToolLicenseInfo(final ToolLicenseInfo toolLicenseInfo) {
		Mockito.doReturn(toolLicenseInfo).when(this.workbenchDataManager).saveOrUpdateToolLicenseInfo(toolLicenseInfo);
	}

	@Test
	public void testGetLicenseInfo() {
		// add a dummy data in the cache so that we can test the scenario
		// where an outdated license info for bv is retrieved separately from the database,
		// saved after update and put to the cache
		// as buildToolLicenseCache is already called inside loadToolLicenseCache which has a separate test
		ToolLicenseUtil.toolLicenseCache.put(ToolName.mbdt.toString(),
				this.toolLicenseInfoInitializer.createToolLicenseInfo(ToolName.mbdt.toString()));
		final String expectedToolName = ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME;
		final ToolLicenseInfo expectedToolLicenseInfo = this.mockGetToolLicenseInfoByToolName(expectedToolName);
		final String outdatedLicensePath = expectedToolLicenseInfo.getLicensePath();
		final String outdatedLicenseHash = expectedToolLicenseInfo.getLicenseHash();
		final Date outdatedExpirationDate = expectedToolLicenseInfo.getExpirationDate();

		this.mockSaveOrUpdateToolLicenseInfo(expectedToolLicenseInfo);

		// call method to test
		final ToolLicenseInfo actualToolLicenseInfo = ToolLicenseUtilTest.toolLicenseUtil.getLicenseInfo(expectedToolName);
		// verify
		this.assertExpectedData(expectedToolLicenseInfo, actualToolLicenseInfo, outdatedLicensePath, outdatedLicenseHash,
				outdatedExpirationDate);
	}

	private ToolLicenseInfo mockGetToolLicenseInfoByToolName(final String toolName) {
		final ToolLicenseInfo toolLicenseInfo = this.toolLicenseInfoInitializer.createToolLicenseInfo(toolName);
		toolLicenseInfo.setLicenseInfoId(1);
		Mockito.doReturn(toolLicenseInfo).when(this.workbenchDataManager).getToolLicenseInfoByToolName(toolName);
		return toolLicenseInfo;
	}

	@Test
	public void testIsOutdatedWithOutdatedLicensePath() {
		final ToolLicenseInfo outdatedToolLicenseInfo =
				this.toolLicenseInfoInitializer.createToolLicenseInfo(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);

		// call method to test
		final boolean isOutdated = ToolLicenseUtilTest.toolLicenseUtil.isOutdated(outdatedToolLicenseInfo);
		Assert.assertTrue("The license info is expected to be outdated", isOutdated);

		// assert that the license path is outdated
		final String updatedLicensePath =
				this.toolLicenseInfoInitializer.getLicensePathFromWBInstallationDir(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		Assert.assertFalse("The license path should not be equal", updatedLicensePath.equals(outdatedToolLicenseInfo.getLicensePath()));
	}

	@Test
	public void testIsOutdatedWithOutdatedLicenseHash() {
		final ToolLicenseInfo outdatedToolLicenseInfo =
				this.toolLicenseInfoInitializer.createToolLicenseInfo(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		final String updatedLicensePath =
				this.toolLicenseInfoInitializer.getLicensePathFromWBInstallationDir(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		outdatedToolLicenseInfo.setLicensePath(updatedLicensePath);

		// call method to test
		final boolean isOutdated = ToolLicenseUtilTest.toolLicenseUtil.isOutdated(outdatedToolLicenseInfo);
		Assert.assertTrue("The license info is expected to be outdated", isOutdated);

		// assert that the license hash is outdated
		final String updatedLicenseHash = ToolLicenseUtilTest.toolLicenseUtil.getLicenseHash(updatedLicensePath);
		Assert.assertFalse("The license hash should not be equal", updatedLicenseHash.equals(outdatedToolLicenseInfo.getLicenseHash()));
	}

	@Test
	public void testIsOutdatedWithUpdatedLicensePathAndHash() {
		final ToolLicenseInfo outdatedToolLicenseInfo =
				this.toolLicenseInfoInitializer.createToolLicenseInfo(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		final String updatedLicensePath =
				this.toolLicenseInfoInitializer.getLicensePathFromWBInstallationDir(ToolLicenseInfoInitializer.BREEDING_VIEW_TOOL_NAME);
		outdatedToolLicenseInfo.setLicensePath(updatedLicensePath);
		final String updatedLicenseHash = ToolLicenseUtilTest.toolLicenseUtil.getLicenseHash(updatedLicensePath);
		outdatedToolLicenseInfo.setLicenseHash(updatedLicenseHash);

		// call method to test
		final boolean isOutdated = ToolLicenseUtilTest.toolLicenseUtil.isOutdated(outdatedToolLicenseInfo);
		Assert.assertFalse("The license info is expected to be updated", isOutdated);
	}

	@Test
	public void testIsExpiredWithNoDate() {
		final boolean isExpired = ToolLicenseUtilTest.toolLicenseUtil.isExpired(null);
		Assert.assertFalse("It should not be expired as there's no expiration date", isExpired);
	}

	@Test
	public void testIsExpiredWithExpiredDate() {
		final boolean isExpired = ToolLicenseUtilTest.toolLicenseUtil.isExpired(this.sampleBVLicenseExpirationDate);
		Assert.assertTrue("It should be expired", isExpired);
	}

	@Test
	public void testIsExpiredWithCurrentDate() {
		final boolean isExpired = ToolLicenseUtilTest.toolLicenseUtil.isExpired(DateUtil.getCurrentDateWithZeroTime());
		Assert.assertTrue("It should be expired", isExpired);
	}

	@Test
	public void testIsExpiredWithDateASecondAfterCurrentDate() {
		final Calendar calendar = DateUtil.getCalendarInstance();
		calendar.setTime(DateUtil.getCurrentDateWithZeroTime());
		calendar.add(Calendar.SECOND, 1);
		final boolean isExpired = ToolLicenseUtilTest.toolLicenseUtil.isExpired(calendar.getTime());
		Assert.assertFalse("It should not be expired", isExpired);
	}

	@Test
	public void testIsExpiredWithDateASecondBeforeCurrentDate() {
		final Calendar calendar = DateUtil.getCalendarInstance();
		calendar.setTime(DateUtil.getCurrentDateWithZeroTime());
		calendar.add(Calendar.SECOND, -1);
		final boolean isExpired = ToolLicenseUtilTest.toolLicenseUtil.isExpired(calendar.getTime());
		Assert.assertTrue("It should be expired", isExpired);
	}

	@Test
	public void testIsExpiringWithinThirtyDaysWithNoDate() {
		final boolean isExpiringIn30Days = ToolLicenseUtilTest.toolLicenseUtil.isExpiringWithinThirtyDays(null);
		Assert.assertFalse("It should not be expiring in thirty days as there's no expiration date", isExpiringIn30Days);
	}

	@Test
	public void testIsExpiringWithinThirtyDaysWithExpiredDate() {
		final boolean isExpiringIn30Days =
				ToolLicenseUtilTest.toolLicenseUtil.isExpiringWithinThirtyDays(this.sampleBVLicenseExpirationDate);
		Assert.assertFalse("It should not be expiring in thirty days as it's already expired", isExpiringIn30Days);
	}

	@Test
	public void testIsExpiringWithinThirtyDaysWithCurrentDate() {
		final boolean isExpiringIn30Days =
				ToolLicenseUtilTest.toolLicenseUtil.isExpiringWithinThirtyDays(DateUtil.getCurrentDateWithZeroTime());
		Assert.assertFalse("It should not be expiring in thirty days as it's already expired", isExpiringIn30Days);
	}

	@Test
	public void testIsExpiringWithinThirtyDaysWithCurrentDatePlus30Days() {
		final Calendar calendar = DateUtil.getCalendarInstance();
		calendar.setTime(DateUtil.getCurrentDateWithZeroTime());
		calendar.add(Calendar.DAY_OF_MONTH, 30);
		final boolean isExpiringIn30Days = ToolLicenseUtilTest.toolLicenseUtil.isExpiringWithinThirtyDays(calendar.getTime());
		Assert.assertTrue("It should be expiring in thirty days", isExpiringIn30Days);
	}

	@Test
	public void testIsExpiringWithinThirtyDaysWithCurrentDatePlus1Day() {
		final Calendar calendar = DateUtil.getCalendarInstance();
		calendar.setTime(DateUtil.getCurrentDateWithZeroTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		final boolean isExpiringIn30Days = ToolLicenseUtilTest.toolLicenseUtil.isExpiringWithinThirtyDays(calendar.getTime());
		Assert.assertTrue("It should be expiring in thirty days", isExpiringIn30Days);
	}

}
