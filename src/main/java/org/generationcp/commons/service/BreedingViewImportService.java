package org.generationcp.commons.service;

import java.io.File;
import java.util.Map;

import org.generationcp.commons.exceptions.BreedingViewImportException;

public interface BreedingViewImportService {

	void importMeansData(File file, int studyId) throws BreedingViewImportException;
	void importMeansData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException;
	void importSummaryStatsData(File file, int studyId) throws BreedingViewImportException;
	void importSummaryStatsData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException;
	void importOutlierData(File file, int studyId) throws BreedingViewImportException;
	void importOutlierData(File file, int studyId, Map<String, String> localNameToAliasMap) throws BreedingViewImportException;
	boolean isValidMeansData(File file);
	boolean isValidOutlierData(File file);
	boolean isValidSummaryStatsData(File file);
	
}
