package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.parsing.pojo.ImportedCrosses;

public class ImportedCrossesTestDataInitializer {
	public ImportedCrosses createImportedCrosses(final boolean hasHybridMethod){
		ImportedCrosses importedCross = new ImportedCrosses();
		final String method = hasHybridMethod ? "TCR" : "UGM";
		importedCross.setRawBreedingMethod(method);
		return importedCross;
	}

	public List<ImportedCrosses> createImportedCrossesList(final int count, final boolean hasHybridMethod){
		List<ImportedCrosses> importedCrossesList = new ArrayList<ImportedCrosses>();
		for(int ctr  = 0; ctr<count; ctr++){
			importedCrossesList.add(createImportedCrosses(hasHybridMethod));
		}
		return importedCrossesList;
	}
	
}
