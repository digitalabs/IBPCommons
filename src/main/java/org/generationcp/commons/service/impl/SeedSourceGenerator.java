
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
			final String plotNumber, final String studyName) {

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

		final Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.NAME, nameResolver);
		keyComponentValueResolvers.put(KeyComponent.LOCATION, new LocationResolver(workbook, instanceNumber));
		keyComponentValueResolvers.put(KeyComponent.SEASON, new SeasonResolver(SeedSourceGenerator.this.ontologyVariableDataManager,
				SeedSourceGenerator.this.contextUtil, workbook, instanceNumber));
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);

		return service
				.generateKey(new SeedSourceTemplateProvider(this.germplasmNamingProperties, workbook.getStudyDetails().getStudyType(),
						this.contextUtil.getProjectInContext().getCropType().getCropName()), keyComponentValueResolvers);
	}

	public String generateSeedSourceForCross(final Workbook workbook, final String malePlotNo, final String femalePlotNo,
			final String maleStudyName, final String femaleStudyName) {
		// Cross scenario is currently only for Nurseries, hard coding instance number to 1 is fine until that is not the case.
		String femaleSeedSource = generateSeedSource(workbook, "1", null, femalePlotNo, femaleStudyName);
		String maleSeedSource = generateSeedSource(workbook, "1", null, malePlotNo, maleStudyName);
		return femaleSeedSource + "/" + maleSeedSource;
	}
}
