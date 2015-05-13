package org.generationcp.commons.util;

import java.util.List;

import junit.framework.Assert;

import org.generationcp.commons.pojo.CustomReportType;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.junit.Test;

public class CustomReportTypeUtilTest {
	@Test
	public void testReadReportConfiguration(){
		StandardPreset standardPreset = new StandardPreset();
		standardPreset.setConfiguration("<?xml version=\"1.0\"?><reports><profile>CIMMYT</profile><report><code>WLBL05</code><name>labels without design, wheat</name></report><report><code>WLBL21</code><name>labels with design, wheat</name></report></reports>");
		List<CustomReportType> customReportTypes = CustomReportTypeUtil.readReportConfiguration(standardPreset, "CIMMYT");
		Assert.assertEquals("Should be able to parse 2 reports", 2, customReportTypes.size());
		Assert.assertEquals("1st report code should be WLBL05", "WLBL05", customReportTypes.get(0).getCode());
		Assert.assertEquals("2nd report code should be WLBL21", "WLBL21", customReportTypes.get(1).getCode());		
	}
}
