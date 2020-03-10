package org.generationcp.commons.ruleengine.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.commons.parsing.pojo.ImportedCrosses;
import org.generationcp.commons.ruleengine.resolver.KeyComponentValueResolver;
import org.generationcp.commons.ruleengine.resolver.LocationResolver;
import org.generationcp.commons.ruleengine.resolver.SeasonResolver;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SeedSourceGenerator {

	private static final String MULTIPARENT_BEGIN_CHAR = "[";
	private static final String MULTIPARENT_END_CHAR = "]";
	private static final String SEEDSOURCE_SEPARATOR = "/";

	@Resource
	private GermplasmNamingProperties germplasmNamingProperties;

	@Resource
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Resource
	private ContextUtil contextUtil;

	public SeedSourceGenerator() {

	}

	public String generateSeedSource(final ObservationUnitRow observationUnitRow,
		final List<MeasurementVariable> conditions, final String selectionNumber,
		final String plotNumber, final String studyName, final String plantNumber, final Map<String, String> locationIdNameMap,
		final List<MeasurementVariable> environmentVariables) {

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

		final Map<Integer, MeasurementVariable> environmentVariablesByTermId =
			environmentVariables.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, v -> v));


		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.PLANT_NO, plantNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.LOCATION,
			new LocationResolver(conditions, observationUnitRow, locationIdNameMap));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
			new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, observationUnitRow,
				environmentVariablesByTermId));

		return service.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties,
			this.contextUtil.getProjectInContext().getCropType().getCropName()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Pair<ObservationUnitRow, ObservationUnitRow> environmentRow,
		final Pair<List<MeasurementVariable>, List<MeasurementVariable>> conditions,
		final Pair<Map<String, String>, Map<String, String>> locationIdNameMap,
		final Pair<List<MeasurementVariable>, List<MeasurementVariable>> environmentVariables, final ImportedCrosses crossInfo) {

		final List<String> generatedSeedSources = new ArrayList<>();

		final Integer femalePlotNo = crossInfo.getFemalePlotNo();
		final String femaleSeedSource =
			this.generateSeedSource(environmentRow.getLeft(),
				conditions.getLeft(), null, femalePlotNo != null? femalePlotNo.toString() : "", crossInfo.getFemaleStudyName(), null,
				locationIdNameMap.getLeft(), environmentVariables.getLeft());

		final List<Integer> malePlotNos = crossInfo.getMalePlotNos();
		for (final Integer malePlotNo : malePlotNos) {
			final String maleSeedSource =
				this.generateSeedSource(environmentRow.getRight(),
					conditions.getRight(), null, malePlotNo != null? malePlotNo.toString() : "", crossInfo.getMaleStudyNames().get(0), null, locationIdNameMap.getRight(),
					environmentVariables.getRight());
			generatedSeedSources.add(maleSeedSource);
		}
		if (malePlotNos.size() > 1) {
			return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + SeedSourceGenerator.MULTIPARENT_BEGIN_CHAR
				+ StringUtils.join(generatedSeedSources, ", ") + SeedSourceGenerator.MULTIPARENT_END_CHAR;
		}

		return femaleSeedSource + SeedSourceGenerator.SEEDSOURCE_SEPARATOR + generatedSeedSources.get(0);
	}


	protected void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

}
