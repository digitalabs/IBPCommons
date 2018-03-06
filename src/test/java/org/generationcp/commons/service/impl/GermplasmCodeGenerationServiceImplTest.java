package org.generationcp.commons.service.impl;

import com.google.common.collect.Lists;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmCodeGenerationServiceImplTest {

	@Mock
	private RulesService rulesService;

	@Mock
	private RuleFactory ruleFactory;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmGroupingService germplasmGroupingService;

	@Mock
	private KeySequenceRegisterService keySequenceRegisterService;

	@InjectMocks
	private GermplasmCodeGenerationServiceImpl germplasmCodeGenerationService = new GermplasmCodeGenerationServiceImpl();

	private static final String PREFIX = "ABH";
	private static final String SUFFIX = "CDE";
	private static final Integer NEXT_NUMBER = 21;
	private static final Integer NEXT_NUMBER_WITH_SPACE = 6;
	private static final Integer NEXT_NUMBER_WITHOUT_PREFIX = 31;
	private GermplasmNameSetting setting;
	private UserDefinedField codeNameType;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.setting = this.createGermplasmNameSetting();
		this.setupCodeNameType();

		Mockito.doReturn(NEXT_NUMBER).when(this.keySequenceRegisterService).getNextSequence(PREFIX, SUFFIX);
		Mockito.doReturn(NEXT_NUMBER_WITH_SPACE).when(this.keySequenceRegisterService).getNextSequence(PREFIX + " ", " " + SUFFIX);
		Mockito.doReturn(NEXT_NUMBER).when(this.keySequenceRegisterService).incrementAndGetNextSequence(PREFIX, SUFFIX);
		Mockito.doReturn(NEXT_NUMBER_WITH_SPACE).when(this.keySequenceRegisterService)
				.incrementAndGetNextSequence(PREFIX + " ", " " + SUFFIX);
		Mockito.doReturn(NEXT_NUMBER_WITHOUT_PREFIX).when(this.keySequenceRegisterService).getNextSequence(PREFIX, "");
	}

	@Test
	public void testApplyGroupNameWhenGermplasmIsNotFixed() {
		Germplasm g1 = new Germplasm();
		g1.setGid(1);

		Mockito.when(this.germplasmDataManager.getGermplasmByGID(g1.getGid())).thenReturn(g1);

		GermplasmGroupNamingResult result =
				this.germplasmCodeGenerationService.applyGroupName(g1.getGid(), this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with one validation message regarding germplasm not being fixed.", 1,
				result.getMessages().size());
		Assert.assertTrue("Expected service to return with validation regarding germplasm not being fixed.",
				result.getMessages().contains("Germplasm (gid: 1) is not part of a management group. Can not assign group name."));
	}

	private void setupCodeNameType() {
		this.codeNameType = new UserDefinedField();
		this.codeNameType.setFldno(41);
		this.codeNameType.setFcode("CODE1");
	}

	@Test
	public void testApplyGroupNameWhenGermplasmIsFixedAndHasGroupMembers() {
		Integer mgid = 1;

		Germplasm g1 = new Germplasm();
		g1.setGid(1);
		g1.setMgid(mgid);

		// Setup existing preferred name
		Name g1Name = new Name();
		g1Name.setNval("g1Name");
		g1Name.setNstat(1);
		g1.getNames().add(g1Name);

		Mockito.when(this.germplasmDataManager.getGermplasmByGID(g1.getGid())).thenReturn(g1);

		Germplasm g2 = new Germplasm();
		g2.setGid(2);
		g2.setMgid(mgid);

		Germplasm g3 = new Germplasm();
		g3.setGid(3);
		g3.setMgid(mgid);

		Mockito.when(this.germplasmGroupingService.getGroupMembers(mgid)).thenReturn(Lists.newArrayList(g1, g2, g3));

		String expectedCodedName = PREFIX + " 000000" + NEXT_NUMBER_WITH_SPACE + " " + SUFFIX;

		GermplasmGroupNamingResult result =
				this.germplasmCodeGenerationService.applyGroupName(g1.getGid(), this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with 3 messages, one per group member.", 3, result.getMessages().size());

		Assert.assertEquals("Expected germplasm g1 to have a coded name assigned as preferred name.", expectedCodedName,
				g1.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g1 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g1.findPreferredName().getTypeId());
		Assert.assertEquals("Expected existing preferred name of germplasm g1 to be set as non-preferred.", new Integer(0),
				g1Name.getNstat());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g1.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(0));

		Assert.assertEquals("Expected germplasm g2 to have a coded name assigned.", expectedCodedName, g2.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g2 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g2.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g2.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(1));

		Assert.assertEquals("Expected germplasm g3 to have a coded name assigned.", expectedCodedName, g3.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g3 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g3.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g3.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(2));
	}

	@Test
	public void testApplyGroupNameWhenGermplasmIsFixedAndHasGroupMembersWithExistingCodedNames() {
		Integer mgid = 1;

		Germplasm g1 = new Germplasm();
		g1.setGid(1);
		g1.setMgid(mgid);

		Mockito.when(this.germplasmDataManager.getGermplasmByGID(g1.getGid())).thenReturn(g1);

		Germplasm g2 = new Germplasm();
		g2.setGid(2);
		g2.setMgid(mgid);

		Germplasm g3 = new Germplasm();
		g3.setGid(3);
		g3.setMgid(mgid);

		// Lets setup the third member with existing coded name.
		Name g3CodedName = new Name();
		// same name type
		g3CodedName.setTypeId(this.codeNameType.getFldno());
		// but different name
		String existingCodedNameOfG3 = "ExistingCodedNameOfG3";
		g3CodedName.setNval(existingCodedNameOfG3);
		g3CodedName.setNstat(1);
		g3.getNames().add(g3CodedName);

		Mockito.when(this.germplasmGroupingService.getGroupMembers(mgid)).thenReturn(Lists.newArrayList(g1, g2, g3));

		String expectedCodedName = PREFIX + " 000000" + NEXT_NUMBER_WITH_SPACE + " " + SUFFIX;

		GermplasmGroupNamingResult result =
				this.germplasmCodeGenerationService.applyGroupName(g1.getGid(), this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with 3 messages, one per group member.", 3, result.getMessages().size());

		Assert.assertEquals("Expected germplasm g1 to have a coded name assigned as preferred name.", expectedCodedName,
				g1.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g1 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g1.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g1.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(0));

		Assert.assertEquals("Expected germplasm g2 to have a coded name assigned.", expectedCodedName, g2.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g2 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g2.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g2.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(1));

		Assert.assertEquals("Expected existing coded name of g3 to be retained.", existingCodedNameOfG3, g3.findPreferredName().getNval());
		Assert.assertTrue(
				"Expected service to return with validation regarding germplasm g3 not assigned given name because it already has one with same type.",
				result.getMessages().contains(
						"Germplasm (gid: 3) already has existing name ExistingCodedNameOfG3 of type CODE1. Supplied name "
								+ expectedCodedName + " was not added."));
	}

	@Test
	public void testApplyGroupNameWhenStartNumberIsSpecified() {
		// Set a start number greater than what is saved
		final Integer startNumber = NEXT_NUMBER_WITH_SPACE + 10;
		this.setting.setStartNumber(startNumber);

		Integer mgid = 1;

		Germplasm g1 = new Germplasm();
		g1.setGid(1);
		g1.setMgid(mgid);

		// Setup existing preferred name
		Name g1Name = new Name();
		g1Name.setNval("g1Name");
		g1Name.setNstat(1);
		g1.getNames().add(g1Name);

		Mockito.when(this.germplasmDataManager.getGermplasmByGID(g1.getGid())).thenReturn(g1);

		Germplasm g2 = new Germplasm();
		g2.setGid(2);
		g2.setMgid(mgid);

		Germplasm g3 = new Germplasm();
		g3.setGid(3);
		g3.setMgid(mgid);

		Mockito.when(this.germplasmGroupingService.getGroupMembers(mgid)).thenReturn(Lists.newArrayList(g1, g2, g3));

		String expectedCodedName = PREFIX + " 00000" + startNumber + " " + SUFFIX;

		GermplasmGroupNamingResult result =
				this.germplasmCodeGenerationService.applyGroupName(g1.getGid(), this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with 3 messages, one per group member.", 3, result.getMessages().size());

		Assert.assertEquals("Expected germplasm g1 to have a coded name assigned as preferred name.", expectedCodedName,
				g1.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g1 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g1.findPreferredName().getTypeId());
		Assert.assertEquals("Expected existing preferred name of germplasm g1 to be set as non-preferred.", new Integer(0),
				g1Name.getNstat());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g1.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(0));

		Assert.assertEquals("Expected germplasm g2 to have a coded name assigned.", expectedCodedName, g2.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g2 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g2.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g2.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(1));

		Assert.assertEquals("Expected germplasm g3 to have a coded name assigned.", expectedCodedName, g3.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g3 to have a coded name with coded name type.", this.codeNameType.getFldno(),
				g3.findPreferredName().getTypeId());
		Assert.assertEquals(String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", g3.getGid(),
				expectedCodedName, this.codeNameType.getFcode()), result.getMessages().get(2));
	}

	@Test
	public void testApplyGroupNames() {
		final Set<Integer> gids = new HashSet<>(Arrays.asList(1001, 1002, 1003, 1004));
		final Map<Integer, Germplasm> germplasmMap = new HashMap<>();
		final Map<Integer, Name> oldPreferredNames = new HashMap<>();
		Integer startNumber = NEXT_NUMBER_WITH_SPACE;
		for (final Integer gid : gids) {
			Germplasm germplasm = new Germplasm();
			germplasm.setGid(gid);
			germplasm.setMgid(gid);

			// Setup existing preferred name
			Name g1Name = new Name();
			g1Name.setNval("Name G-" + gid);
			g1Name.setNstat(1);
			germplasm.getNames().add(g1Name);
			germplasmMap.put(gid, germplasm);
			oldPreferredNames.put(gid, g1Name);

			Mockito.when(this.germplasmDataManager.getGermplasmByGID(gid)).thenReturn(germplasm);
			Mockito.when(this.germplasmGroupingService.getGroupMembers(gid)).thenReturn(Lists.newArrayList(germplasm));
		}
		Mockito.when(this.keySequenceRegisterService.getNextSequence(PREFIX + " ", " " + SUFFIX))
				.thenReturn(startNumber, startNumber + 1, startNumber + 2, startNumber + 3, startNumber + 4, startNumber + 5);

		final Map<Integer, GermplasmGroupNamingResult> resultsMap =
				this.germplasmCodeGenerationService.applyGroupNames(gids, this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with " + gids.size() + " naming results, one per germplasm.", gids.size(),
				resultsMap.keySet().size());

		for (final Integer gid : gids) {
			final Germplasm germplasm = germplasmMap.get(gid);
			String expectedCodedName = PREFIX + " 000000" + (startNumber++) + " " + SUFFIX;
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name assigned as preferred name.", expectedCodedName,
					germplasm.findPreferredName().getNval());
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name with coded name type.", this.codeNameType.getFldno(),
					germplasm.findPreferredName().getTypeId());
			Assert.assertEquals("Expected existing preferred name of germplasm " + gid + " to be set as non-preferred.", new Integer(0),
					oldPreferredNames.get(gid).getNstat());

			Assert.assertNotNull(resultsMap.get(gid));
			Assert.assertEquals(1, resultsMap.get(gid).getMessages().size());
			Assert.assertEquals(
					String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", germplasm.getGid(),
							expectedCodedName, this.codeNameType.getFcode()), resultsMap.get(gid).getMessages().get(0));
		}

	}

	@Test
	public void testApplyGroupNamesWithStartNumberSmallerThanLastSequenceUsed() {
		// Set a start number greater than what is saved
		this.setting.setStartNumber(NEXT_NUMBER_WITH_SPACE - 1);

		final Set<Integer> gids = new HashSet<>(Arrays.asList(1001, 1002, 1003, 1004));
		final Map<Integer, Germplasm> germplasmMap = new HashMap<>();
		final Map<Integer, Name> oldPreferredNames = new HashMap<>();
		Integer startNumber = NEXT_NUMBER_WITH_SPACE;
		for (final Integer gid : gids) {
			Germplasm germplasm = new Germplasm();
			germplasm.setGid(gid);
			germplasm.setMgid(gid);

			// Setup existing preferred name
			Name g1Name = new Name();
			g1Name.setNval("Name G-" + gid);
			g1Name.setNstat(1);
			germplasm.getNames().add(g1Name);
			germplasmMap.put(gid, germplasm);
			oldPreferredNames.put(gid, g1Name);

			Mockito.when(this.germplasmDataManager.getGermplasmByGID(gid)).thenReturn(germplasm);
			Mockito.when(this.germplasmGroupingService.getGroupMembers(gid)).thenReturn(Lists.newArrayList(germplasm));
		}
		Mockito.when(this.keySequenceRegisterService.getNextSequence(PREFIX + " ", " " + SUFFIX))
				.thenReturn(startNumber, startNumber + 1, startNumber + 2, startNumber + 3, startNumber + 4, startNumber + 5);

		final Map<Integer, GermplasmGroupNamingResult> resultsMap =
				this.germplasmCodeGenerationService.applyGroupNames(gids, this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with " + gids.size() + " naming results, one per germplasm.", gids.size(),
				resultsMap.keySet().size());

		for (final Integer gid : gids) {
			final Germplasm germplasm = germplasmMap.get(gid);
			String expectedCodedName = PREFIX + " 000000" + (startNumber++) + " " + SUFFIX;
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name assigned as preferred name.", expectedCodedName,
					germplasm.findPreferredName().getNval());
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name with coded name type.", this.codeNameType.getFldno(),
					germplasm.findPreferredName().getTypeId());
			Assert.assertEquals("Expected existing preferred name of germplasm " + gid + " to be set as non-preferred.", new Integer(0),
					oldPreferredNames.get(gid).getNstat());

			Assert.assertNotNull(resultsMap.get(gid));
			Assert.assertEquals(1, resultsMap.get(gid).getMessages().size());
			Assert.assertEquals(
					String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", germplasm.getGid(),
							expectedCodedName, this.codeNameType.getFcode()), resultsMap.get(gid).getMessages().get(0));
		}

	}

	@Test
	public void testApplyGroupNamesWhenValidStartNumberIsSpecified() {
		// Set a start number greater than what is saved
		Integer startNumber = NEXT_NUMBER_WITH_SPACE + 10;
		this.setting.setStartNumber(startNumber);

		final Set<Integer> gids = new HashSet<>(Arrays.asList(1001, 1002, 1003));
		final Map<Integer, Germplasm> germplasmMap = new HashMap<>();
		final Map<Integer, Name> oldPreferredNames = new HashMap<>();
		for (final Integer gid : gids) {
			Germplasm germplasm = new Germplasm();
			germplasm.setGid(gid);
			germplasm.setMgid(gid);

			// Setup existing preferred name
			Name g1Name = new Name();
			g1Name.setNval("Name G-" + gid);
			g1Name.setNstat(1);
			germplasm.getNames().add(g1Name);
			germplasmMap.put(gid, germplasm);
			oldPreferredNames.put(gid, g1Name);

			Mockito.when(this.germplasmDataManager.getGermplasmByGID(gid)).thenReturn(germplasm);
			Mockito.when(this.germplasmGroupingService.getGroupMembers(gid)).thenReturn(Lists.newArrayList(germplasm));
		}

		final Map<Integer, GermplasmGroupNamingResult> resultsMap =
				this.germplasmCodeGenerationService.applyGroupNames(gids, this.setting, this.codeNameType, 0, 0);
		Assert.assertEquals("Expected service to return with " + gids.size() + " naming results, one per germplasm.", gids.size(),
				resultsMap.keySet().size());

		for (final Integer gid : gids) {
			final Germplasm germplasm = germplasmMap.get(gid);
			final String expectedCodedName = PREFIX + " 00000" + (startNumber++) + " " + SUFFIX;
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name assigned as preferred name.", expectedCodedName,
					germplasm.findPreferredName().getNval());
			Assert.assertEquals("Expected germplasm " + gid + " to have a coded name with coded name type.", this.codeNameType.getFldno(),
					germplasm.findPreferredName().getTypeId());
			Assert.assertEquals("Expected existing preferred name of germplasm " + gid + " to be set as non-preferred.", new Integer(0),
					oldPreferredNames.get(gid).getNstat());

			Assert.assertNotNull(resultsMap.get(gid));
			Assert.assertEquals(1, resultsMap.get(gid).getMessages().size());
			Assert.assertEquals(
					String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", germplasm.getGid(),
							expectedCodedName, this.codeNameType.getFcode()), resultsMap.get(gid).getMessages().get(0));
		}

	}

	@Test
	public void testBuildDesignationNameInSequenceDefaultSetting() {
		final GermplasmNameSetting defaultSetting = new GermplasmNameSetting();
		defaultSetting.setPrefix(GermplasmCodeGenerationServiceImplTest.PREFIX);
		defaultSetting.setSuffix(GermplasmCodeGenerationServiceImplTest.SUFFIX);
		defaultSetting.setAddSpaceBetweenPrefixAndCode(false);
		defaultSetting.setAddSpaceBetweenSuffixAndCode(false);

		final int nextNumber = 10;
		final String designationName = this.germplasmCodeGenerationService.buildDesignationNameInSequence(nextNumber, defaultSetting);
		Assert.assertEquals(PREFIX + nextNumber + SUFFIX, designationName);
	}

	@Test
	public void testBuildDesignationNameInSequenceWithSpacesInPrefixSuffix() {
		final int nextNumber = 10;
		final String designationName = this.germplasmCodeGenerationService.buildDesignationNameInSequence(nextNumber, this.setting);
		Assert.assertEquals(PREFIX + " 00000" + nextNumber + " " + SUFFIX, designationName);
	}

	@Test
	public void testBuildPrefixStringDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setPrefix(" A  ");
		final String prefix = this.germplasmCodeGenerationService.buildPrefixString(setting);
		Assert.assertEquals("A", prefix);
	}

	@Test
	public void testBuildPrefixStringWithSpace() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setPrefix("   A");
		setting.setAddSpaceBetweenPrefixAndCode(true);
		final String prefix = this.germplasmCodeGenerationService.buildPrefixString(setting);
		Assert.assertEquals("A ", prefix);
	}

	@Test
	public void testBuildSuffixStringDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setSuffix("  B   ");
		final String suffix = this.germplasmCodeGenerationService.buildSuffixString(setting);
		Assert.assertEquals("B", suffix);
	}

	@Test
	public void testBuildSuffixStringWithSpace() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setSuffix("   B   ");
		setting.setAddSpaceBetweenSuffixAndCode(true);
		final String suffix = this.germplasmCodeGenerationService.buildSuffixString(setting);
		Assert.assertEquals(" B", suffix);
	}

	@Test
	public void testGetNextNumberInSequenceDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setPrefix(PREFIX);

		final int nextNumber = this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		Assert.assertEquals(GermplasmCodeGenerationServiceImplTest.NEXT_NUMBER_WITHOUT_PREFIX.intValue(), nextNumber);
		final ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> suffixCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(prefixCaptor.capture(), suffixCaptor.capture());
		Assert.assertEquals(PREFIX, prefixCaptor.getValue());
		Assert.assertEquals("", suffixCaptor.getValue());
	}

	@Test
	public void testGetNextNumberInSequenceWhenPrefixIsEmpty() {

		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setStartNumber(1);
		setting.setPrefix("");

		final int nextNumber = this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		Assert.assertEquals(1, nextNumber);
		Mockito.verify(this.keySequenceRegisterService, Mockito.never()).getNextSequence(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testGetNextNumberInSequenceWhenSuffixIsSupplied() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		final String prefix = "A";
		setting.setPrefix(prefix);
		final String suffix = "CDE";
		setting.setSuffix(suffix);

		this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		final ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> suffixCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(prefixCaptor.capture(), suffixCaptor.capture());
		Assert.assertEquals(prefix, prefixCaptor.getValue());
		Assert.assertEquals(suffix, suffixCaptor.getValue());
	}

	@Test
	public void testGetNextNumberInSequenceWhenSpaceSuppliedBetweenPrefixAndCode() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		final String prefix = "A";
		setting.setPrefix(prefix);
		setting.setAddSpaceBetweenPrefixAndCode(true);
		final String suffix = "CDE";
		setting.setSuffix(suffix);

		this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		final ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> suffixCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(prefixCaptor.capture(), suffixCaptor.capture());
		Assert.assertEquals(prefix + " ", prefixCaptor.getValue());
		Assert.assertEquals(suffix, suffixCaptor.getValue());
	}

	@Test
	public void testGetNextNumberInSequenceWhenSpaceSuppliedBetweenSuffixAndCode() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		final String prefix = "A";
		setting.setPrefix(prefix);
		final String suffix = "CDE";
		setting.setSuffix(suffix);
		setting.setAddSpaceBetweenSuffixAndCode(true);

		this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		final ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> suffixCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(prefixCaptor.capture(), suffixCaptor.capture());
		Assert.assertEquals(prefix, prefixCaptor.getValue());
		Assert.assertEquals(" " + suffix, suffixCaptor.getValue());
	}

	@Test
	public void testGetNextNumberInSequenceWhenSpaceSuppliedAfterPrefixAndBeforeSuffix() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		final String prefix = "A";
		setting.setPrefix(prefix);
		setting.setAddSpaceBetweenPrefixAndCode(true);
		final String suffix = "CDE";
		setting.setSuffix(suffix);
		setting.setAddSpaceBetweenSuffixAndCode(true);

		this.germplasmCodeGenerationService.getNextNumberInSequence(setting);
		final ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> suffixCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.keySequenceRegisterService).getNextSequence(prefixCaptor.capture(), suffixCaptor.capture());
		Assert.assertEquals(prefix + " ", prefixCaptor.getValue());
		Assert.assertEquals(" " + suffix, suffixCaptor.getValue());
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringDefault() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setNumOfDigits(0);
		final String formattedString = this.germplasmCodeGenerationService.getNumberWithLeadingZeroesAsString(1, setting);
		Assert.assertEquals("1", formattedString);
	}

	@Test
	public void testGetNumberWithLeadingZeroesAsStringWithNumOfDigitsSpecified() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();
		setting.setNumOfDigits(8);
		final String formattedString = this.germplasmCodeGenerationService.getNumberWithLeadingZeroesAsString(1, setting);
		Assert.assertEquals("00000001", formattedString);
	}

	@Test
	public void testGetNextNameInSequenceWithNullStartNumber() {
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmCodeGenerationService.getNextNameInSequence(this.setting);
		} catch (InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals(buildExpectedNextName(), nextNameInSequence);
	}

	@Test
	public void testGetNextNameInSequenceWithZeroStartNumber() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		setting.setStartNumber(0);
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmCodeGenerationService.getNextNameInSequence(setting);
		} catch (InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals(this.buildExpectedNextName(), nextNameInSequence);
	}

	private String buildExpectedNextName() {
		return GermplasmCodeGenerationServiceImplTest.PREFIX + " 000000" + NEXT_NUMBER_WITH_SPACE + " "
				+ GermplasmCodeGenerationServiceImplTest.SUFFIX;
	}

	@Test
	public void testGetNextNameInSequenceWhenSpecifiedSequenceStartingNumberIsGreater() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		final int startNumber = 1000;
		setting.setStartNumber(startNumber);
		String nextNameInSequence = "";
		try {
			nextNameInSequence = this.germplasmCodeGenerationService.getNextNameInSequence(setting);
		} catch (InvalidGermplasmNameSettingException e) {
			Assert.fail("Not expecting InvalidGermplasmNameSettingException to be thrown but was thrown.");
		}
		Assert.assertEquals("The specified starting sequence number will be used since it's larger.",
				GermplasmCodeGenerationServiceImplTest.PREFIX + " 000" + startNumber + " " + GermplasmCodeGenerationServiceImplTest.SUFFIX,
				nextNameInSequence);
	}

	@Test
	public void testGetNextNameInSequenceWhenSpecifiedSequenceStartingNumberIsLower() {
		final GermplasmNameSetting setting = this.createGermplasmNameSetting();
		final int startNumber = GermplasmCodeGenerationServiceImplTest.NEXT_NUMBER_WITH_SPACE - 1;
		setting.setStartNumber(startNumber);
		try {
			this.germplasmCodeGenerationService.getNextNameInSequence(setting);
			Assert.fail("Expecting InvalidGermplasmNameSettingException to be thrown but was not.");
		} catch (InvalidGermplasmNameSettingException e) {
			Assert.assertEquals(
					"Starting sequence number should be higher than or equal to next name in the sequence: " + this.buildExpectedNextName()
							+ ".", e.getMessage());
		}
	}

	private GermplasmNameSetting createGermplasmNameSetting() {
		final GermplasmNameSetting setting = new GermplasmNameSetting();

		setting.setPrefix(GermplasmCodeGenerationServiceImplTest.PREFIX);
		setting.setSuffix(GermplasmCodeGenerationServiceImplTest.SUFFIX);
		setting.setAddSpaceBetweenPrefixAndCode(true);
		setting.setAddSpaceBetweenSuffixAndCode(true);
		setting.setNumOfDigits(7);

		return setting;
	}

}
