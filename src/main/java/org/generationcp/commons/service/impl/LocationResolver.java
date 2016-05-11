package org.generationcp.commons.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationResolver implements KeyComponentValueResolver {

	protected List<MeasurementVariable> conditions;
	protected MeasurementRow trailInstanceObservation;
	protected StudyType studyType;

	private static final Logger LOG = LoggerFactory.getLogger(LocationResolver.class);

	public LocationResolver(List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyType studyType) {
		this.studyType = studyType;
		this.trailInstanceObservation = trailInstanceObservation;
		this.conditions = conditions;
	}

	@Override
	public String resolve() {
		String location = "";

		if(this.studyType == StudyType.N){

			MeasurementVariable locationAbbrVariable = null;

			Integer locationId = TermId.LOCATION_ABBR.getId();
			if (this.conditions != null) {
				for (MeasurementVariable mv : this.conditions) {
					if (mv.getTermId() == locationId) {
						locationAbbrVariable = mv;
					}
				}
			}
			if (locationAbbrVariable != null) {
				location = locationAbbrVariable.getValue();
			}

		}else if(this.studyType == StudyType.T){

			if (this.trailInstanceObservation != null) {
				for (MeasurementData trialInstanceMeasurement : this.trailInstanceObservation.getDataList()) {
					if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == TermId.LOCATION_ABBR.getId()) {
						location = trialInstanceMeasurement.getValue();
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(location)) {
			LOG.debug(
					"No LOCATION_ABBR(8189) variable was found or it is present but no value is set. "
							+ "Resolving location value to be an empty string.");
			return "";
		}

		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
