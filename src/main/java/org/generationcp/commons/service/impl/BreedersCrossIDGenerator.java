package org.generationcp.commons.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyCodeGenerationService;
import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

public class BreedersCrossIDGenerator {

	private GermplasmNamingProperties germplasmNamingProperties;
	private OntologyVariableDataManager ontologyVariableDataManager;
	private ContextUtil contextUtil;

	public BreedersCrossIDGenerator(GermplasmNamingProperties germplasmNamingProperties, OntologyVariableDataManager ontologyVariableDataManager,
			ContextUtil contextUtil) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
	}

	public String generateBreedersCrossID(final Workbook workbook, final String instanceNumber) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX, new ProjectPrefixResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, workbook, instanceNumber));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION, new HabitatDesignationResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, workbook, instanceNumber));
		keyComponentValueResolvers.put(KeyComponent.SEASON, new SeasonResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, workbook, instanceNumber));
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(workbook, instanceNumber));

		return service.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties, workbook.getStudyDetails().getStudyType()),
				keyComponentValueResolvers);
	}
}
