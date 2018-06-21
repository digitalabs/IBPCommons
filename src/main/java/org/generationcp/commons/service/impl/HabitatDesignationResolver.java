
package org.generationcp.commons.service.impl;

import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitatDesignationResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(HabitatDesignationResolver.class);

	public HabitatDesignationResolver(final OntologyVariableDataManager ontologyVariableDataManager, final ContextUtil contextUtil,
			final List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation, final StudyTypeDto studyType) {
		super(ontologyVariableDataManager, contextUtil, conditions, trailInstanceObservation, studyType);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.HABITAT_DESIGNATION;
	}

	@Override
	protected boolean isAbbreviationRequired() {
		return true;
	}

	@Override
	protected String getDefaultValue() {
		HabitatDesignationResolver.LOG.debug("No Habitat_Designation(3002) variable was found or it is present but no value is set."
				+ "Resolving Habitat value to be an empty string.");
		return "";
	}

	@Override
	protected String getValueFromTrialInstanceMeasurementData(final MeasurementData measurementData) {
		return measurementData.getValue();
	}

	@Override
	protected String getValueFromTrialConditions(final MeasurementVariable trialCondition) {
		return trialCondition.getValue();
	}
}
