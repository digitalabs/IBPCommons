package org.generationcp.commons.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import com.google.common.base.Optional;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Revolves Season value for Nurseries and Trials.
 */
public class SeasonResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(SeasonResolver.class);

	public SeasonResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyType studyType) {
		super(ontologyVariableDataManager, contextUtil, conditions, trailInstanceObservation, studyType);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.SEASON_VAR;
	}

	@Override
	protected boolean isAbbreviationRequired() {
		return true;
	}

	@Override
	protected String getDefaultValue() {
		// Default the season to current year and month.
		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());
		LOG.debug(
				"No Crop_season_Code(8371) variable was found or it is present but no value is set. Defaulting [SEASON] with: {}.",
				currentYearAndMonth);
		return currentYearAndMonth;
	}

	@Override
	protected String getValueFromTrialInstanceMeasurementData(final MeasurementData measurementData) {

		final Optional<ValueReference> valueReferenceOptional = findValueReferenceByDescription(measurementData.getValue(), measurementData.getMeasurementVariable().getPossibleValues());

		if (valueReferenceOptional.isPresent()) {
			return valueReferenceOptional.get().getName();
		} else {
			return measurementData.getValue();
		}


	}

	protected Optional<ValueReference> findValueReferenceByDescription(final String description, final List<ValueReference> possibleValues) {

		if (possibleValues != null) {
			for (ValueReference valueReference : possibleValues) {
				if (valueReference.getDescription().equals(description)) {
					return Optional.of(valueReference);
				}
			}
		}

		return Optional.absent();

	}

}
