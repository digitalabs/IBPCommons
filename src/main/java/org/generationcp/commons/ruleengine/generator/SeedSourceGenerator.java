package org.generationcp.commons.ruleengine.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.commons.ruleengine.resolver.LocationResolver;
import org.generationcp.commons.ruleengine.resolver.SeasonResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import static org.generationcp.middleware.service.api.dataset.ObservationUnitUtils.fromMeasurementRow;

public class SeedSourceGenerator {

	private static final String MULTIPARENT_BEGIN_CHAR = "[";
	private static final String MULTIPARENT_END_CHAR = "]";
	private static final String INSTANCE_NUMBER = "1";
	private static final String SEEDSOURCE_SEPARATOR = "/";

	protected static final List<Integer> ENVIRONMENT_VARIABLE_TYPES = Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId());

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private DatasetService datasetService;

	public SeedSourceGenerator() {

	}

	@SuppressWarnings("Duplicates")
	public String generateSeedSource(final Integer studyId, final Integer environmentDatasetId, final ObservationUnitRow obsevationUnitRow,
		final List<MeasurementVariable> conditions, final String selectionNumber,
		final String plotNumber,
		final String studyName, final String plantNumber) {

		if ("0".equals(plotNumber)) {
			return Name.UNKNOWN;
		}

		final KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		final KeyComponentValueResolver nameResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return studyName;
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver plotNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return plotNumber;
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver selectionNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return selectionNumber;
			}

			@Override
			public boolean isOptional() {
				return true;
			}
		};

		final KeyComponentValueResolver plantNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return plantNumber;
			}

			@Override
			public boolean isOptional() {
				// Setting to not optional so that "-" wont be appended before plant number when it is specified
				return false;
			}
		};

		final Map<String, String> locationIdNameMap =
			this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

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
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.PLANT_NO, plantNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.LOCATION,
			new LocationResolver(conditions, obsevationUnitRow, locationIdNameMap));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
			new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, obsevationUnitRow,
				environmentVariablesByTermId));

		return service.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties,
			this.contextUtil.getProjectInContext().getCropType().getCropName()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Workbook femaleWorkbook, final List<String> malePlotNos, final String femalePlotNo,
		final String maleStudyName, final String femaleStudyName, final Workbook maleWorkbook) {

		final List<String> generatedSeedSources = new ArrayList<>();

		final String femaleSeedSource =
			this.generateSeedSource(femaleWorkbook.getStudyDetails().getId(), femaleWorkbook.getTrialDatasetId(), fromMeasurementRow(
				femaleWorkbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(SeedSourceGenerator.INSTANCE_NUMBER))),
				femaleWorkbook.getConditions(), null, femalePlotNo, femaleStudyName, null);

		for (final String malePlotNo : malePlotNos) {
			final String maleSeedSource =
				this.generateSeedSource(maleWorkbook.getStudyDetails().getId(), maleWorkbook.getTrialDatasetId(), fromMeasurementRow(
					maleWorkbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(SeedSourceGenerator.INSTANCE_NUMBER))),
					maleWorkbook.getConditions(), null, malePlotNo, maleStudyName, null);
			generatedSeedSources.add(maleSeedSource);
		}
		if (malePlotNos.size() > 1) {
			return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + SeedSourceGenerator.MULTIPARENT_BEGIN_CHAR
				+ StringUtils.join(generatedSeedSources, ", ") + SeedSourceGenerator.MULTIPARENT_END_CHAR;
		}

		return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + generatedSeedSources.get(0);
	}

	public String generateSeedSourceForCross(final Workbook workbook, final List<String> malePlotNos, final String femalePlotNo,
		final String maleStudyName, final String femaleStudyName) {

		//for single study context where male and female workbook is the same.
		return this.generateSeedSourceForCross(workbook, malePlotNos, femalePlotNo, maleStudyName, femaleStudyName, workbook);
	}

	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

}
