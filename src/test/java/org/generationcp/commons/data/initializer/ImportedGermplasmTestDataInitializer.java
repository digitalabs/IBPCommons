package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;

public class ImportedGermplasmTestDataInitializer {

	public static ImportedGermplasm createImportedGermplasm() {
		final ImportedGermplasm importedGermplasm = new ImportedGermplasm();
		importedGermplasm.setIndex(1);
		importedGermplasm.setEntryId(1);
		importedGermplasm.setEntryCode("1");
		importedGermplasm.setDesig("Desig");
		importedGermplasm.setGid("1");
		importedGermplasm.setGroupId(1);
		importedGermplasm.setSource("SOURCE");
		importedGermplasm.setStockIDs("STOCKID");
		importedGermplasm.setEntryCode("1");
		importedGermplasm.setCross("CROSS");
		return importedGermplasm;
	}

	public static List<ImportedGermplasm> createImportedGermplasmList() {
		final List<ImportedGermplasm> list = new ArrayList<>();
		list.add(ImportedGermplasmTestDataInitializer.createImportedGermplasm());
		return list;
	}
}
