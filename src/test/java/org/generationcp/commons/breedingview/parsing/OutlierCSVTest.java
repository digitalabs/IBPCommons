
package org.generationcp.commons.breedingview.parsing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class OutlierCSVTest {

	private static final List<String> PLOTS = Arrays.asList("2");
	private static final List<String> FACTORS_LIST = Arrays.asList("TRIAL_INSTANCE", TermId.PLOT_NO.name());
	private static final List<String> TRAITS_LIST = Arrays.asList("ASI", "Aphid1_5", "EPH", "FMSROT");

	private OutlierCSV outlierCSV;

	@Before
	public void setup() throws URISyntaxException {
		final File file = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutlier.csv").toURI());
		this.outlierCSV = new OutlierCSV(file, new HashMap<String, String>());
	}

	@Test
	public void testGetTrialHeader() throws IOException {
		Assert.assertEquals(OutlierCSVTest.FACTORS_LIST.get(0), this.outlierCSV.getTrialHeader());
	}

	@Test
	public void testGetHeaderTraits() throws IOException {
		Assert.assertEquals(OutlierCSVTest.TRAITS_LIST, this.outlierCSV.getHeaderTraits());
	}

	@Test
	public void testGetHeaders() throws IOException {
		final List<String> allHeaders = new ArrayList<>();
		allHeaders.addAll(OutlierCSVTest.FACTORS_LIST);
		allHeaders.addAll(OutlierCSVTest.TRAITS_LIST);
		Assert.assertEquals(allHeaders, this.outlierCSV.getHeaders());
	}

	@Test
	public void testGetData() throws IOException {
		final Map<String, Map<String, List<String>>> data = this.outlierCSV.getData();

		// Expecting only Trial Environment "1" to be parsed
		final String environmentName = "1";
		Assert.assertEquals(1, data.keySet().size());
		Assert.assertEquals(environmentName, data.keySet().iterator().next());

		// Check outlier data for plot analyzed for environment
		Assert.assertEquals(OutlierCSVTest.PLOTS.size(), data.get(environmentName).size());
		final List<String> outliersForPlot = data.get(environmentName).get(OutlierCSVTest.PLOTS.get(0));
		Assert.assertEquals(OutlierCSVTest.TRAITS_LIST.size(), outliersForPlot.size());
		Assert.assertEquals(Arrays.asList("9", "8", "", "5"), outliersForPlot);
	}

}
