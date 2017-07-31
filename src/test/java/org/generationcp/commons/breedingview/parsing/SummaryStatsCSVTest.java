
package org.generationcp.commons.breedingview.parsing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class SummaryStatsCSVTest {

	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final List<String> SUMMARY_STATS_LIST = Arrays.asList("MEAN", "MEANSED", "HERITABILITY", "PVALUE");
	private static final List<String> TRAITS_LIST = Arrays.asList("ASI", "Aphid1_5", "EPH", "FMSROT");
	private SummaryStatsCSV summaryStatsCSV;

	@Before
	public void setup() throws URISyntaxException {
		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
		this.summaryStatsCSV = new SummaryStatsCSV(file, new HashMap<String, String>());
	}

	@Test
	public void testValidate() throws URISyntaxException {
		try {
			this.summaryStatsCSV.validate();
		} catch (final BreedingViewInvalidFormatException e) {
			Assert.fail("Not expecting exception for a valid summary file.");
		}

		// Validate invalid file
		try {
			final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());
			this.summaryStatsCSV = new SummaryStatsCSV(file, new HashMap<String, String>());
			this.summaryStatsCSV.validate();
			Assert.fail("Expecting exception to be thrown for an invalid summary file but none thrown.");
		} catch (final BreedingViewInvalidFormatException e) {
			Assert.assertEquals(SummaryStatsCSV.FORMAT_IS_INVALID_FOR_SUMMARY_STATISTICS, e.getMessage());
		}
	}

	@Test
	public void testGetHeaders() throws IOException {
		final List<String> allHeaders = new ArrayList<>();
		allHeaders.add(SummaryStatsCSVTest.TRIAL_INSTANCE);
		allHeaders.add("TRAIT");
		allHeaders.addAll(SummaryStatsCSVTest.SUMMARY_STATS_LIST);

		final List<String> headers = this.summaryStatsCSV.getHeaders();

		Assert.assertEquals(allHeaders.size(), headers.size());
		final ListIterator<String> expectedHeadersIterator = allHeaders.listIterator();
		final ListIterator<String> actualHeadersIterator = headers.listIterator();
		while (expectedHeadersIterator.hasNext()) {
			Assert.assertEquals(expectedHeadersIterator.next().toUpperCase(), actualHeadersIterator.next().toUpperCase());
		}
	}

	@Test
	public void testGetSummaryHeaders() throws IOException {
		final List<String> summaryHeaders = this.summaryStatsCSV.getSummaryHeaders();

		Assert.assertEquals(SummaryStatsCSVTest.SUMMARY_STATS_LIST.size(), summaryHeaders.size());
		final ListIterator<String> expectedHeadersIterator = SummaryStatsCSVTest.SUMMARY_STATS_LIST.listIterator();
		final ListIterator<String> actualHeadersIterator = summaryHeaders.listIterator();
		while (expectedHeadersIterator.hasNext()) {
			Assert.assertEquals(expectedHeadersIterator.next().toUpperCase(), actualHeadersIterator.next().toUpperCase());
		}

	}

	@Test
	public void testGetTrialHeader() throws IOException {
		Assert.assertEquals(SummaryStatsCSVTest.TRIAL_INSTANCE, this.summaryStatsCSV.getTrialHeader());
	}

	@Test
	public void testGetData() throws IOException {
		final String environmentName = "1";
		final Map<String, Map<String, List<String>>> summaryData = this.summaryStatsCSV.getData();

		// Expecting only Trial Environment "1" to be parsed
		Assert.assertEquals(1, summaryData.keySet().size());
		Assert.assertEquals(environmentName, summaryData.keySet().iterator().next());

		// Check traits analyzed for environment
		Assert.assertEquals(SummaryStatsCSVTest.TRAITS_LIST.size(), summaryData.get(environmentName).size());
		for (final String trait : SummaryStatsCSVTest.TRAITS_LIST) {
			Assert.assertNotNull(summaryData.get(environmentName).get(trait));
		}

		// Check list of values parsed for trait "ASI"
		final int expectedSummaryMethodsSize = SummaryStatsCSV.SUMMARY_STATS_METHODS.length;
		final List<String> valuesForTrait1 = summaryData.get(environmentName).get(SummaryStatsCSVTest.TRAITS_LIST.get(0));
		Assert.assertEquals(expectedSummaryMethodsSize, valuesForTrait1.size());
		Assert.assertEquals("4.77777777777778", valuesForTrait1.get(0).toUpperCase());
		Assert.assertEquals("1.64053588643061", valuesForTrait1.get(1).toUpperCase());
		Assert.assertEquals("1.33970838533948E-007", valuesForTrait1.get(2).toUpperCase());
		Assert.assertEquals("0.59277772260922", valuesForTrait1.get(3).toUpperCase());

		final List<String> valuesForTrait2 = summaryData.get(environmentName).get(SummaryStatsCSVTest.TRAITS_LIST.get(1));
		Assert.assertEquals(expectedSummaryMethodsSize, valuesForTrait2.size());
		Assert.assertEquals("5.44444444444444", valuesForTrait2.get(0).toUpperCase());
		Assert.assertEquals("2.44948959352742", valuesForTrait2.get(1).toUpperCase());
		Assert.assertEquals("8.0036829175345E-007", valuesForTrait2.get(2).toUpperCase());
		Assert.assertEquals("0.633313208454948", valuesForTrait2.get(3).toUpperCase());

		final List<String> valuesForTrait3 = summaryData.get(environmentName).get(SummaryStatsCSVTest.TRAITS_LIST.get(2));
		Assert.assertEquals(expectedSummaryMethodsSize, valuesForTrait3.size());
		Assert.assertEquals("4.75", valuesForTrait3.get(0).toUpperCase());
		Assert.assertEquals("1.9824941711781", valuesForTrait3.get(1).toUpperCase());
		Assert.assertEquals("0.755758436827121", valuesForTrait3.get(2).toUpperCase());
		Assert.assertEquals("0.0127968957025755", valuesForTrait3.get(3).toUpperCase());

		final List<String> valuesForTrait4 = summaryData.get(environmentName).get(SummaryStatsCSVTest.TRAITS_LIST.get(3));
		Assert.assertEquals(expectedSummaryMethodsSize, valuesForTrait4.size());
		Assert.assertEquals("3", valuesForTrait4.get(0).toUpperCase());
		Assert.assertEquals("1.80534185194117", valuesForTrait4.get(1).toUpperCase());
		Assert.assertEquals("1.91999971810297E-007", valuesForTrait4.get(2).toUpperCase());
		Assert.assertEquals("0.761300383072833", valuesForTrait4.get(3).toUpperCase());
	}

}
