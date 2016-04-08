package org.generationcp.commons.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyCodeGenerationService;
import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Method;

public class BreedersCrossIDGenerator {

	private GermplasmNamingProperties germplasmNamingProperties;
	private OntologyVariableDataManager ontologyVariableDataManager;
	private ContextUtil contextUtil;
	private GermplasmDataManager germplasmDataManager;

	public BreedersCrossIDGenerator(GermplasmNamingProperties germplasmNamingProperties, OntologyVariableDataManager ontologyVariableDataManager,
			ContextUtil contextUtil, GermplasmDataManager germplasmDataManager) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
		this.germplasmDataManager = germplasmDataManager;
	}

	public String generateBreedersCrossID(final StudyType studyType, final List<MeasurementVariable> conditions,
			final MeasurementRow trailInstanceObservation, final Method breedingMethod, final ImportedGermplasm importedGermplasm,
			final int selectionNumber) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final KeyComponentValueResolver selectionNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return String.valueOf(selectionNumber);
			}

			@Override
			public boolean isOptional() {
				return true;
			}
		};

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX, new ProjectPrefixResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION, new HabitatDesignationResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.SEASON, new SeasonResolver(BreedersCrossIDGenerator.this.ontologyVariableDataManager,
				BreedersCrossIDGenerator.this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.CROSS_TYPE, new CrossTypeResolver(studyType, contextUtil, breedingMethod, importedGermplasm, germplasmDataManager));

		return service.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties, studyType),
				keyComponentValueResolvers);
	}
}
