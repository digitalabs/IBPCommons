
package org.generationcp.commons.spring.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ToolLicenseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This is a spring bean managed utility for getting information about licenses of tools used in BMS
 */
@Configurable
public class ToolLicenseUtil {

	static final Logger LOG = LoggerFactory.getLogger(ToolLicenseUtil.class);

	private static ToolLicenseUtil instance;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Value("${workbench.installation_dir}")
	private String workbenchInstallationDirectory;

	private ToolLicenseUtil() {
		// private constructor
	}

	public static ToolLicenseUtil getInstance() {
		if (ToolLicenseUtil.instance == null) {
			ToolLicenseUtil.instance = new ToolLicenseUtil();
		}
		return ToolLicenseUtil.instance;
	}

	/**
	 * Main goal is to prevent excessive queries to get local user names. This is a global cache that will expire every 10 minutes.
	 */
	private static Cache<String, ToolLicenseInfo> toolLicenseCache = CacheBuilder.newBuilder().maximumSize(500)
			.expireAfterWrite(10, TimeUnit.MINUTES).build();

	public void loadToolLicenseCache() {
		this.buildToolLicenseCache();
		final ConcurrentMap<String, ToolLicenseInfo> toolLicenseMap = ToolLicenseUtil.toolLicenseCache.asMap();
		for (final String toolName : toolLicenseMap.keySet()) {
			ToolLicenseInfo licenseInfo = toolLicenseMap.get(toolName);
			if (licenseInfo != null && this.isOutdated(licenseInfo)) {
				licenseInfo = this.updateLicenseInfoBasedOnLicenseFile(licenseInfo);
				ToolLicenseUtil.toolLicenseCache.put(toolName, licenseInfo);
			}
		}
	}

	private boolean isOutdated(final ToolLicenseInfo licenseInfo) {
		final String licensePath = this.getLicensePath(licenseInfo);
		if (!licenseInfo.getLicensePath().equals(licensePath)) {
			return true;
		}
		if (StringUtils.isEmpty(licenseInfo.getLicenseHash())) {
			return true;
		}
		final String md5 = this.getLicenseHash(licensePath);
		if (!licenseInfo.getLicenseHash().equals(md5)) {
			return true;
		}
		return false;
	}

	public ToolLicenseInfo getLicenseInfo(final String toolName) {
		if (ToolLicenseUtil.toolLicenseCache == null) {
			this.buildToolLicenseCache();
		}
		ToolLicenseInfo licenseInfo = ToolLicenseUtil.toolLicenseCache.getIfPresent(toolName);
		if (licenseInfo == null) {
			licenseInfo = this.getLicenseInfoByToolName(toolName);
		}
		if (licenseInfo != null && this.isOutdated(licenseInfo)) {
			licenseInfo = this.updateLicenseInfoBasedOnLicenseFile(licenseInfo);
			ToolLicenseUtil.toolLicenseCache.put(licenseInfo.getTool().getToolName(), licenseInfo);
		}
		return licenseInfo;
	}

	private ToolLicenseInfo getLicenseInfoByToolName(final String toolName) {
		return this.workbenchDataManager.getToolLicenseInfoByToolName(toolName);
	}

	public ToolLicenseInfo updateLicenseInfoBasedOnLicenseFile(final ToolLicenseInfo licenseInfo) {
		final String licensePath = this.getLicensePath(licenseInfo);
		licenseInfo.setLicensePath(licensePath);

		final String licenseHash = this.getLicenseHash(licensePath);
		licenseInfo.setLicenseHash(licenseHash);

		final Date expirationDate = this.getExpirationDate(licenseInfo);
		licenseInfo.setExpirationDate(expirationDate);

		return this.workbenchDataManager.saveOrUpdateToolLicenseInfo(licenseInfo);
	}

	private Date getExpirationDate(final ToolLicenseInfo licenseInfo) {
		Date expirationDate = null;
		final File file = new File(licenseInfo.getLicensePath());
		if (file.exists()) {
			expirationDate = this.parseLicenseAndGetExpirationDate(file, licenseInfo);
		}
		return expirationDate;
	}

	private Date parseLicenseAndGetExpirationDate(final File file, final ToolLicenseInfo licenseInfo) {
		Date expirationDate = null;
		final String[] fileContent = this.getFileContent(file);
		if (fileContent == null || fileContent.length == 0) {
			return expirationDate;
		}

		boolean expirationDateFound = false;
		for (int i = 0, length = fileContent.length; i < length && !expirationDateFound; i++) {
			final String text = fileContent[i];
			// filter out comments inside the file
			if (text == null || text.startsWith("#")) {
				continue;
			}

			// at the time of implementation, the pattern of expiration date is dd-MMM-yyyy (example: "30-jun-2016")
			// make sure to check the tool name in the future to make the expiration date pattern and format appropriate
			final String patternString = "[0-3]\\d-[a-zA-Z][a-zA-Z][a-zA-Z]-\\d{4}";
			final String expirationDateFormat = "dd-MMM-yyyy";
			final List<String> patternMatches = this.getPatternMatches(patternString, text);

			for (final String match : patternMatches) {
				final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat(expirationDateFormat);
				try {
					expirationDate = EXPIRATION_DATE_FORMAT.parse(match);
					expirationDateFound = true;
				} catch (final ParseException e) {
					ToolLicenseUtil.LOG.error(e.getMessage(), e);
				}
			}
		}
		return expirationDate;
	}

	private List<String> getPatternMatches(final String patternString, final String text) {
		final List<String> patternMatches = new ArrayList<>();
		final Pattern pattern = Pattern.compile(patternString);
		final Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			final String match = matcher.group(0);
			if (match != null) {
				patternMatches.add(matcher.group(0));
			}
		}

		return patternMatches;
	}

	private String[] getFileContent(final File file) {
		final List<String> fileContent = new ArrayList<>();
		BufferedReader bufferedReader = null;
		try {
			String currentLine = null;
			bufferedReader = new BufferedReader(new FileReader(file));
			while ((currentLine = bufferedReader.readLine()) != null) {
				fileContent.add(currentLine);
			}
		} catch (final IOException e) {
			ToolLicenseUtil.LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (final IOException e) {
				ToolLicenseUtil.LOG.error(e.getMessage(), e);
			}
		}
		return fileContent.toArray(new String[fileContent.size()]);
	}

	private String getLicenseHash(final String licensePath) {
		FileInputStream fis = null;
		String md5 = null;
		try {
			final File file = new File(licensePath);
			if (file.exists()) {
				fis = new FileInputStream(file);
				md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			}
		} catch (final FileNotFoundException e) {
			ToolLicenseUtil.LOG.error(e.getMessage(), e);
		} catch (final IOException e) {
			ToolLicenseUtil.LOG.error(e.getMessage(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					ToolLicenseUtil.LOG.error(e.getMessage(), e);
				}
			}
		}
		return md5;
	}

	private String getLicensePath(final ToolLicenseInfo licenseInfo) {
		String licensePath = licenseInfo.getLicensePath();

		// if the tool path is an absolute path and the workbench installation directory has been set,
		// update the license path from the specified installation directory
		final int startIndex = licensePath.indexOf("tools");
		if (startIndex > 0 && this.workbenchInstallationDirectory != null) {
			licensePath = this.workbenchInstallationDirectory + File.separator + licensePath.substring(startIndex);
		}
		return licensePath;
	}

	private void buildToolLicenseCache() {
		final List<ToolLicenseInfo> listOfToolLicenseInfo = this.workbenchDataManager.getListOfToolLicenseInfo();
		for (final ToolLicenseInfo toolLicenseInfo : listOfToolLicenseInfo) {
			ToolLicenseUtil.toolLicenseCache.put(toolLicenseInfo.getTool().getToolName(), toolLicenseInfo);
		}
	}

	public boolean isExpired(final Date expirationDate) {
		final Date currentDate = DateUtil.getCurrentDateWithZeroTime();
		if (expirationDate != null && currentDate.compareTo(expirationDate) >= 0) {
			return true;
		}
		return false;
	}

	public boolean isExpiringWithinThirtyDays(final Date expirationDate) {
		if (expirationDate != null) {
			final int daysBeforeExpiration = this.daysBeforeExpiration(expirationDate);
			if (daysBeforeExpiration <= 30 && daysBeforeExpiration > 0) {
				return true;
			}
		}
		return false;
	}

	public int daysBeforeExpiration(final Date expirationDate) {
		if (expirationDate != null) {
			final Date currentDate = DateUtil.getCurrentDateWithZeroTime();
			final long msBeforeExpiration = expirationDate.getTime() - currentDate.getTime();
			final long daysBeforeExpiration = TimeUnit.DAYS.convert(msBeforeExpiration, TimeUnit.MILLISECONDS);
			return new Long(daysBeforeExpiration).intValue();
		}
		return 0;
	}

	public boolean isToolExpired(final String toolName) {
		final ToolLicenseInfo licenseInfo = this.getLicenseInfo(toolName);
		final Date expirationDate = licenseInfo.getExpirationDate();
		return this.isExpired(expirationDate);
	}

	public boolean isToolExpiringWithinThirtyDays(final String toolName) {
		final ToolLicenseInfo licenseInfo = this.getLicenseInfo(toolName);
		final Date expirationDate = licenseInfo.getExpirationDate();
		return this.isExpiringWithinThirtyDays(expirationDate);
	}

	public int daysBeforeToolExpiration(final String toolName) {
		final ToolLicenseInfo licenseInfo = this.getLicenseInfo(toolName);
		final Date expirationDate = licenseInfo.getExpirationDate();
		return this.daysBeforeExpiration(expirationDate);
	}
}
