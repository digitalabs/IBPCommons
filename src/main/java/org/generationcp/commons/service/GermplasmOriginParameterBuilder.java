package org.generationcp.commons.service;

import org.generationcp.commons.parsing.pojo.ImportedCrosses;
import org.generationcp.middleware.domain.etl.Workbook;

public interface GermplasmOriginParameterBuilder {

	GermplasmOriginGenerationParameters build(Workbook workbook, String trialInstanceNumber, String replicationNumber,
			String selectionNumber, String plotNumber);
	
	GermplasmOriginGenerationParameters build(Workbook workbook, ImportedCrosses cross);
}
