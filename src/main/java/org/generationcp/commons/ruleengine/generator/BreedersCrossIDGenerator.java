package org.generationcp.commons.ruleengine.generator;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.commons.ruleengine.resolver.HabitatDesignationResolver;
import org.generationcp.commons.ruleengine.resolver.LocationResolver;
import org.generationcp.commons.ruleengine.resolver.ProjectPrefixResolver;
import org.generationcp.commons.ruleengine.resolver.SeasonResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreedersCrossIDGenerator {

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private StudyDataManager studyDataManager;

	public String generateBreedersCrossID(final int studyId, final StudyTypeDto studyType, final List<MeasurementVariable> conditions,
			final MeasurementRow trailInstanceObservation) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();
		final Map<String, String> locationIdNameMap = studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX,
				new ProjectPrefixResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation,
						studyType));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION,
				new HabitatDesignationResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation,
						studyType));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
				new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers
				.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType, locationIdNameMap));

		return service
				.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties, studyType), keyComponentValueResolvers);
	}

	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
