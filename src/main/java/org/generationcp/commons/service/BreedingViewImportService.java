
package org.generationcp.commons.service;

import java.io.File;

import org.generationcp.commons.exceptions.BreedingViewImportException;

public interface BreedingViewImportService {

	void importMeansData(File file, int studyId) throws BreedingViewImportException;

	void importSummaryStatsData(File file, int studyId) throws BreedingViewImportException;

	void importOutlierData(File file, int studyId) throws BreedingViewImportException;

}
