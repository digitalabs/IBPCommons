package org.generationcp.commons.ruleengine.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.commons.ruleengine.resolver.HabitatDesignationResolver;
import org.generationcp.commons.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.commons.ruleengine.resolver.LocationAbbreviationResolver;
import org.generationcp.commons.ruleengine.resolver.LocationResolver;
import org.generationcp.commons.ruleengine.resolver.ProjectPrefixResolver;
import org.generationcp.commons.ruleengine.resolver.SeasonResolver;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BreedersCrossIDGenerator {

	protected static final List<Integer> ENVIRONMENT_VARIABLE_TYPES = Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId());

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private DatasetService datasetService;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private StudyInstanceService studyInstanceMiddlewareService;

	@SuppressWarnings("Duplicates")
	public String generateBreedersCrossID(final int studyId, final Integer environmentDatasetId,
		final List<MeasurementVariable> conditions, final ObservationUnitRow observationUnitRow) {

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();
		final Map<String, String> locationIdNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);
		final Map<Integer, StudyInstance> studyInstanceMap =
			this.studyInstanceMiddlewareService.getStudyInstances(studyId).stream().collect(
			Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final Map<Integer, MeasurementVariable> environmentVariablesByTermId =
			Maps.uniqueIndex(this.datasetService.getObservationSetVariables(environmentDatasetId, ENVIRONMENT_VARIABLE_TYPES),
				new Function<MeasurementVariable, Integer>() {

					@Nullable
					@Override
					public Integer apply(@Nullable final MeasurementVariable measurementVariable) {
						return measurementVariable.getTermId();
					}
				});

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.PROJECT_PREFIX,
			new ProjectPrefixResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, observationUnitRow,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.HABITAT_DESIGNATION,
			new HabitatDesignationResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, observationUnitRow,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
			new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, observationUnitRow,
				environmentVariablesByTermId));
		keyComponentValueResolvers.put(KeyComponent.LOCATION,
				new LocationResolver(conditions, observationUnitRow, locationIdNameMap));
		keyComponentValueResolvers.put(KeyComponent.LABBR,
			new LocationAbbreviationResolver(observationUnitRow, studyInstanceMap));
		return service
				.generateKey(new BreedersCrossIDTemplateProvider(this.germplasmNamingProperties), keyComponentValueResolvers);
	}

	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
