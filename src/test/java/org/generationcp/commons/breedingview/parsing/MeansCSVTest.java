
package org.generationcp.commons.breedingview.parsing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class MeansCSVTest {

	private static final int NUM_OF_ROWS = 3;
	private static final List<String> FACTORS_LIST = Arrays.asList("TRIAL_INSTANCE", TermId.ENTRY_NO.name(), TermId.GID.name());
	private static final List<String> TRAITS_LIST = Arrays.asList("ASI", "Aphid1_5", "EPH", "FMSROT");

	private MeansCSV meansCSV;

	@Before
	public void setup() throws URISyntaxException {
		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.csv").toURI());
		this.meansCSV = new MeansCSV(file, new HashMap<String, String>());
	}

	@Test
	public void testValidate() throws URISyntaxException {
		try {
			this.meansCSV.validate();
		} catch (final BreedingViewInvalidFormatException e) {
			Assert.fail("Not expecting exception for a valid means file.");
		}

		// Validate invalid file
		try {
			final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSSummary.csv").toURI());
			this.meansCSV = new MeansCSV(file, new HashMap<String, String>());
			this.meansCSV.validate();
			Assert.fail("Expecting exception to be thrown for an invalid means file but none thrown.");
		} catch (final BreedingViewInvalidFormatException e) {
			Assert.assertEquals(MeansCSV.FORMAT_IS_INVALID_FOR_MEANS_DATA, e.getMessage());
		}
	}

	@Test
	public void testGetData() throws IOException {
		assertDataValues();
	}

	@Test
	public void testGetDataWhereEnvironmentFactorIsNotTrialInstance() throws IOException, URISyntaxException {
		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutputWithLocNameEnvFactor.csv").toURI());
		this.meansCSV = new MeansCSV(file, new HashMap<String, String>());
		assertDataValues();
	}

	private void assertDataValues() throws IOException {
		final Map<String, List<String>> data = this.meansCSV.getData();

		// Number of map keys should be equal to sum of factors list + # of traits analyzed. Unit Errors variables are excluded
		Assert.assertEquals(MeansCSVTest.FACTORS_LIST.size() + MeansCSVTest.TRAITS_LIST.size(), data.keySet().size());

		// Check values for TRIAL_INSTANCE - all rows are for trial instance "1"
		final List<String> trialInstanceValues = data.get(MeansCSVTest.FACTORS_LIST.get(0));
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, trialInstanceValues.size());
		Assert.assertEquals(Arrays.asList("1", "1", "1"), trialInstanceValues);

		// Check values for ENTRY_NO
		final List<String> entryNoValues = data.get(MeansCSVTest.FACTORS_LIST.get(1));
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, entryNoValues.size());
		Assert.assertEquals(Arrays.asList("2", "1", "3"), entryNoValues);

		// Check values for GID
		final List<String> gidValues = data.get(MeansCSVTest.FACTORS_LIST.get(2));
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, gidValues.size());
		Assert.assertEquals(Arrays.asList("312200", "34429", "312143"), gidValues);

		// Check values for ASI_Means
		final List<String> valuesForMeans1 = data.get(MeansCSVTest.TRAITS_LIST.get(0) + MeansCSV.MEANS_SUFFIX);
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, valuesForMeans1.size());
		Assert.assertEquals(Arrays.asList("4", "4.66666666666667", "5.66666666666667"), valuesForMeans1);

		// Check values for Aphid1_5_Means
		final List<String> valuesForMeans2 = data.get(MeansCSVTest.TRAITS_LIST.get(1) + MeansCSV.MEANS_SUFFIX);
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, valuesForMeans2.size());
		Assert.assertEquals(Arrays.asList("4.33333333333333", "6.66666666666667", "5.33333333333333"), valuesForMeans2);

		// Check values for EPH_Means
		final List<String> valuesForMeans3 = data.get(MeansCSVTest.TRAITS_LIST.get(2) + MeansCSV.MEANS_SUFFIX);
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, valuesForMeans3.size());
		Assert.assertEquals(Arrays.asList("2.33333333333334", "3.999999968", "7.66666666666667"), valuesForMeans3);

		// Check values for FMSROT_Means
		final List<String> valuesForMeans4 = data.get(MeansCSVTest.TRAITS_LIST.get(3) + MeansCSV.MEANS_SUFFIX);
		Assert.assertEquals(MeansCSVTest.NUM_OF_ROWS, valuesForMeans4.size());
		Assert.assertEquals(Arrays.asList("2.33333333333333", "3", "3.66666666666667"), valuesForMeans4);
	}

}
