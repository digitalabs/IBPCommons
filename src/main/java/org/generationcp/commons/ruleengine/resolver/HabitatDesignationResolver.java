
package org.generationcp.commons.ruleengine.resolver;

import java.util.List;
import java.util.Map;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitatDesignationResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(HabitatDesignationResolver.class);

	public HabitatDesignationResolver(final OntologyVariableDataManager ontologyVariableDataManager, final ContextUtil contextUtil,
		final List<MeasurementVariable> conditions, final ObservationUnitRow observationUnitRow,
		final Map<Integer, MeasurementVariable> measurementVariableByTermId) {

		super(ontologyVariableDataManager, contextUtil, conditions, observationUnitRow, measurementVariableByTermId);
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
	protected String getValueFromObservationUnitData(final ObservationUnitData observationUnitData) {
		return observationUnitData.getValue();
	}

	@Override
	protected String getValueFromTrialConditions(final MeasurementVariable trialCondition) {
		return trialCondition.getValue();
	}
}
