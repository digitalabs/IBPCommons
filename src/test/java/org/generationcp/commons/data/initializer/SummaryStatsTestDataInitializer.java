package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.parsing.SummaryStatsCSV;

public class SummaryStatsTestDataInitializer {
	public static final List<String> TRAITS_LIST = Arrays.asList("ASI", "Aphid1_5", "EPH", "FMSROT");
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final List<String> ENVIRONMENTS_LIST = Arrays.asList("1", "2", "3");
	
	public static List<String> generateHeaders() {
		final List<String> allHeaders = new ArrayList<>();
		allHeaders.add(SummaryStatsTestDataInitializer.TRIAL_INSTANCE);
		allHeaders.add("TRAIT");
		allHeaders.addAll(Arrays.asList(SummaryStatsCSV.SUMMARY_STATS_METHODS));
		
		return allHeaders;
	}
	
	public static Map<String, Map<String, List<String>>> generateData() {
		final  Map<String, Map<String, List<String>>> data = new LinkedHashMap<>();
		for (final String environmentName : ENVIRONMENTS_LIST) {
			data.put(environmentName, new LinkedHashMap<String, List<String>>());
			for (int i = 1; i <= SummaryStatsCSV.SUMMARY_STATS_METHODS.length; i++){
				for (int j= 0; j < TRAITS_LIST.size(); j++) {
					final String trait = TRAITS_LIST.get(j);
					if (data.get(environmentName).get(trait) == null){
						data.get(environmentName).put(trait, new ArrayList<String>());
					}
					final Integer env = Integer.valueOf(environmentName);
					final Integer value = (env * 10) + i;
					data.get(environmentName).get(trait).add(value.toString() + "." + j);
				}
			}
		}
		return data;
	}

}
