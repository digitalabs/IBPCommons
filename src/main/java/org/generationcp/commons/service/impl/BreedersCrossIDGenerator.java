package org.generationcp.commons.service.impl;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyCodeGenerationService;
import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreedersCrossIDGenerator {

	private final GermplasmNamingProperties germplasmNamingProperties;
	private final OntologyVariableDataManager ontologyVariableDataManager;
	private final ContextUtil contextUtil;

	public BreedersCrossIDGenerator(final GermplasmNamingProperties germplasmNamingProperties, final OntologyVariableDataManager ontologyVariableDataManager,
			final ContextUtil contextUtil) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
	}

	public String generateBreedersCrossID(final StudyTypeDto studyType, final List<MeasurementVariable> conditions,
			final MeasurementRow trailInstanceObservation, final Method breedingMethod, final ImportedGermplasm importedGermplasm) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX, new ProjectPrefixResolver(this.ontologyVariableDataManager,
				this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION, new HabitatDesignationResolver(this.ontologyVariableDataManager,
						this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
				new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType));

		return service.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties, studyType),
				keyComponentValueResolvers);
	}
}
