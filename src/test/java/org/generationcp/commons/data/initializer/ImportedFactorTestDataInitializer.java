package org.generationcp.commons.data.initializer;

import org.generationcp.commons.parsing.pojo.ImportedFactor;

public class ImportedFactorTestDataInitializer {
	public static ImportedFactor createImportedFactor() {
		final ImportedFactor importedFactor = new ImportedFactor();
		importedFactor.setFactor("DRVNM");
		importedFactor.setDescription("DERIVATIVE NAME");
		return  importedFactor;
	}

	public static ImportedFactor createImportedFactor(final String factor, final String description) {
		final ImportedFactor importedFactor = new ImportedFactor();
		importedFactor.setFactor(factor);
		importedFactor.setDescription(description);
		return  importedFactor;
	}
}
