package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.CrossNameService;
import org.generationcp.commons.service.SettingsPresetService;
import org.generationcp.commons.settings.BreedingMethodSetting;
import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.commons.settings.CrossSetting;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;

import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public class SettingsPresetServiceTest {

	private SettingsPresetService dut;

	public static final String TEST_SETTING_NAME = "mySettingName";
	public static final Integer TEST_BREEDING_METHOD_ID = 1;
	public static final String SETTING_PREFIX = "PRE";
	public static final String SETTING_SEPARATOR = "-";


	@Before
	public void setup() {
		dut = new SettingsPresetServiceImpl();
	}

	@Test
	public void testConvertToXmlPositive() {
		CrossSetting setting = constructTestSettingObject();

		try {
			String xml = dut.convertPresetSettingToXml(setting, CrossSetting.class);

			assertTrue("Setting was not properly converted to XML", xml.contains("name=\"" + TEST_SETTING_NAME + "\""));
			assertTrue("Setting was not properly converted to XML", xml.contains("methodId=\"" + TEST_BREEDING_METHOD_ID + "\""));
			assertTrue("Setting was not properly converted to XML", xml.contains("prefix=\"" + SETTING_PREFIX + "\""));
			assertTrue("Setting was not properly converted to XML", xml.contains("separator=\"" + SETTING_SEPARATOR + "\""));
		} catch (JAXBException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConvertSettingToObject() {


		try {
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><crossSetting name=\"" + TEST_SETTING_NAME + "\">"
					+ "<breedingMethodSetting basedOnStatusOfParentalLines=\"false\" methodId=\"" + TEST_BREEDING_METHOD_ID + "\"/>"
					+ "<crossNameSetting addSpaceBetweenPrefixAndCode=\"false\" addSpaceBetweenSuffixAndCode=\"false\" prefix=\"" + SETTING_PREFIX + "\" separator=\"" + SETTING_SEPARATOR + "\"/>"
					+ "</crossSetting>";
			CrossSetting converted = (CrossSetting) dut.convertPresetFromXmlString(xml, CrossSetting.class);

			assertEquals("Setting was not properly converted from XML", TEST_SETTING_NAME, converted.getName());
			assertEquals("Setting was not properly converted from XML", TEST_BREEDING_METHOD_ID,
					converted.getBreedingMethodSetting().getMethodId());
			assertEquals("Setting was not properly converted from XML", SETTING_PREFIX,
					converted.getCrossNameSetting().getPrefix());
			assertEquals("Setting was not properly converted from XML", SETTING_SEPARATOR,
								converted.getCrossNameSetting().getSeparator());
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	protected CrossSetting constructTestSettingObject() {
		CrossSetting setting = new CrossSetting();
		setting.setName(TEST_SETTING_NAME);

		BreedingMethodSetting methodSetting = new BreedingMethodSetting(TEST_BREEDING_METHOD_ID,
				false);
		setting.setBreedingMethodSetting(methodSetting);

		CrossNameSetting nameSetting = new CrossNameSetting();
		nameSetting.setPrefix(SETTING_PREFIX);
		nameSetting.setSeparator(SETTING_SEPARATOR);
		setting.setCrossNameSetting(nameSetting);

		return setting;
	}


}
