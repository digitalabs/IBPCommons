package org.generationcp.commons.ruleengine.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.commons.ruleengine.resolver.LocationResolver;
import org.generationcp.commons.ruleengine.resolver.SeasonResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Name;

import javax.annotation.Resource;

public class SeedSourceGenerator {

	private static final String MULTIPARENT_BEGIN_CHAR = "[";
	private static final String MULTIPARENT_END_CHAR = "]";
	private static final String INSTANCE_NUMBER = "1";
	private static final String SEEDSOURCE_SEPARATOR = "/";

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private StudyDataManager studyDataManager;

	public SeedSourceGenerator() {

	}

	public String generateSeedSource(final Workbook workbook, final String instanceNumber, final String selectionNumber,
			final String plotNumber, final String studyName, final String plantNumber) {
		
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

		final List<MeasurementVariable> conditions = workbook.getConditions();
		final StudyTypeDto studyType = workbook.getStudyDetails().getStudyType();
		final MeasurementRow trailInstanceObservation;

		trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(instanceNumber));
		final Map<String, String> locationIdNameMap =
				studyDataManager.createInstanceLocationIdToNameMapFromStudy(workbook.getStudyDetails().getId());

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers
				.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType, locationIdNameMap));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
				new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.PLANT_NO, plantNumberResolver);

		return service.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties, workbook.getStudyDetails().getStudyType(),
				this.contextUtil.getProjectInContext().getCropType().getCropName()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Workbook femaleStudyWorkbook, final List<String> malePlotNos, final String femalePlotNo,
		final String maleStudyName, final String femaleStudyName, final Workbook maleStudyWorkbook) {
		final List<String> generatedSeedSources = new ArrayList<>();
		final String femaleSeedSource =
			generateSeedSource(femaleStudyWorkbook, SeedSourceGenerator.INSTANCE_NUMBER, null, femalePlotNo, femaleStudyName, null);
		for(String malePlotNo: malePlotNos) {
			final String maleSeedSource =
					generateSeedSource(maleStudyWorkbook, SeedSourceGenerator.INSTANCE_NUMBER, null, malePlotNo, maleStudyName, null);
			generatedSeedSources.add(maleSeedSource);
		}
		if(malePlotNos.size() > 1) {
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
