
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
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

public class SeedSourceGenerator {

	private GermplasmNamingProperties germplasmNamingProperties;
	private OntologyVariableDataManager ontologyVariableDataManager;
	private ContextUtil contextUtil;

	public SeedSourceGenerator(GermplasmNamingProperties germplasmNamingProperties, OntologyVariableDataManager ontologyVariableDataManager,
			ContextUtil contextUtil) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
	}

	public String generateSeedSource(final Workbook workbook, final String instanceNumber, final String selectionNumber,
			final String plotNumber, final String studyName, final String plantNumber) {

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

		List<MeasurementVariable> conditions = workbook.getConditions();
		StudyType studyType = workbook.getStudyDetails().getStudyType();
		MeasurementRow trailInstanceObservation = null;

		if(studyType == StudyType.T){
			trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(instanceNumber));
		}

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.SEASON,
				new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, conditions, trailInstanceObservation, studyType));
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.PLANT_NO, plantNumberResolver);

		return service
				.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties, workbook.getStudyDetails().getStudyType(),
						this.contextUtil.getProjectInContext().getCropType().getCropName()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Workbook workbook, final String malePlotNo, final String femalePlotNo,
			final String maleStudyName, final String femaleStudyName) {
		// Cross scenario is currently only for Nurseries, hard coding instance number to 1 is fine until that is not the case.
		String femaleSeedSource = generateSeedSource(workbook, "1", null, femalePlotNo, femaleStudyName, null);
		String maleSeedSource = generateSeedSource(workbook, "1", null, malePlotNo, maleStudyName, null);
		return femaleSeedSource + "/" + maleSeedSource;
	}
}
