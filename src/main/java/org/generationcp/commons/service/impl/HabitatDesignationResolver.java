package org.generationcp.commons.service.impl;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitatDesignationResolver extends CategoricalKeyCodeResolverBase{

	private static final Logger LOG = LoggerFactory.getLogger(HabitatDesignationResolver.class);

	public HabitatDesignationResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, Workbook workbook,
			String instanceNumber) {
		super(ontologyVariableDataManager, contextUtil, workbook, instanceNumber);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.HABITAT_DESIGNATION;
	}

	@Override
	protected String getDefaultValue() {
		LOG.debug(
				"No Habitat_Designation(3002) variable was found or it is present but no value is set, in study: {}."
						+ "Resolving Habitat value to be an empty string.",
				this.workbook.getStudyDetails().getStudyName());
		return "";
	}
}
