package org.generationcp.commons.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyCodeGenerationService;
import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
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

	public String generateBreedersCrossID(final StudyType studyType, final List<MeasurementVariable> conditions,
			final MeasurementRow trailInstanceObservation) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX, new ProjectPrefixResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION, new HabitatDesignationResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.SEASON, new SeasonResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType));

		return service.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties, studyType),
				keyComponentValueResolvers);
	}
}
