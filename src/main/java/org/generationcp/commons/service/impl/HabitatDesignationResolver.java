package org.generationcp.commons.service.impl;

import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitatDesignationResolver extends CategoricalKeyCodeResolverBase{

	private static final Logger LOG = LoggerFactory.getLogger(HabitatDesignationResolver.class);

	public HabitatDesignationResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyType studyType) {
		super(ontologyVariableDataManager, contextUtil, conditions, trailInstanceObservation, studyType);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.HABITAT_DESIGNATION;
	}

	@Override
	protected String getDefaultValue() {
		LOG.debug(
				"No Habitat_Designation(3002) variable was found or it is present but no value is set."
						+ "Resolving Habitat value to be an empty string.");
		return "";
	}
}
