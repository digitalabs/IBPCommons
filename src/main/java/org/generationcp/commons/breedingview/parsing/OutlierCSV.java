
package org.generationcp.commons.breedingview.parsing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.BreedingViewInvalidFormatException;

import au.com.bytecode.opencsv.CSVReader;

/**
 * This class parses a file and creates a map of variable names to a list of outlier values
 *
 */
public class OutlierCSV {

	private static final int ENVIRONMENT_INDEX = 0;
	private static final int PLOT_INDEX = 1;
	private static final int TRAITS_START_INDEX = 2;

	private final File file;
	private Map<String, Map<String, List<String>>> data;
	private final Map<String, String> nameToAliasMapping;
	private String[] header;

	public OutlierCSV(final File file, final Map<String, String> nameToAliasMapping) {
		this.file = file;
		this.nameToAliasMapping = nameToAliasMapping;
	}

	public List<String> getHeaders() throws IOException {

		this.data = this.getData();

		return Arrays.asList(this.header);
	}

	public List<String> getHeaderTraits() throws IOException {

		this.data = this.getData();

		final List<String> list = new ArrayList<String>(Arrays.asList(this.header));
		// Remove the first column header(s) which are the environment and plot headers
		for (int i = 0; i < OutlierCSV.TRAITS_START_INDEX; i++) {
			list.remove(0);
		}
		return list;
	}

	public String getTrialHeader() throws IOException {
		return this.getHeaders().get(OutlierCSV.ENVIRONMENT_INDEX);
	}

	/**
	 * Returns a map of environment names with corresponding map of plot numbers and list of outlier values for traits analyzed
	 *
	 * @return
	 * @throws IOException
	 */
	public Map<String, Map<String, List<String>>> getData() throws IOException {

		if (this.data != null) {
			return this.data;
		}

		final CSVReader reader = new CSVReader(new FileReader(this.file));
		this.data = new LinkedHashMap<>();

		final List<String> list = new ArrayList<String>();
		for (final String aliasLocalName : reader.readNext()) {
			String actualLocalName = null;
			actualLocalName = this.nameToAliasMapping.get(aliasLocalName);
			if (actualLocalName == null) {
				actualLocalName = aliasLocalName;
			}
			list.add(actualLocalName);
		}

		this.header = list.toArray(new String[0]);

		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			final String environment = nextLine[OutlierCSV.ENVIRONMENT_INDEX].trim();
			final String plot = nextLine[OutlierCSV.PLOT_INDEX].trim();

			if (!this.data.containsKey(environment)) {
				this.data.put(environment, new LinkedHashMap<String, List<String>>());
			}

			if (!this.data.get(environment).containsKey(plot)) {
				this.data.get(environment).put(plot, new ArrayList<String>());
			}
			for (int i = OutlierCSV.TRAITS_START_INDEX; i < this.header.length; i++) {
				this.data.get(environment).get(plot).add(nextLine[i].trim());
			}

		}

		reader.close();
		return this.data;
	}

	public void validate() throws BreedingViewInvalidFormatException {

		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(this.file));
			reader.readNext();
			reader.close();
		} catch (final Exception e) {
			throw new BreedingViewInvalidFormatException("A problem occurred while reading the Outlier data file", e);
		}
	}

}
