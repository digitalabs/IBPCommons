package org.generationcp.commons.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationResolver implements KeyComponentValueResolver {

	private Workbook workbook;

	private String instanceNumber;

	private static final Logger LOG = LoggerFactory.getLogger(LocationResolver.class);

	public LocationResolver(Workbook workbook, String instanceNumber) {
		assert workbook != null : "Workbook is required to construct LocationResolver.";
		this.workbook = workbook;
		this.instanceNumber = instanceNumber;
	}

	@Override
	public String resolve() {
		String location = "";
		if (this.workbook.getStudyDetails().getStudyType() == StudyType.N) {
			// For Nurseris, to populate LOCATION placeholder we look for LOCATION_ABBR(8189) variable in general settings.
			MeasurementVariable locationAbbrVariable = this.workbook.findConditionById(TermId.LOCATION_ABBR.getId());
			if (locationAbbrVariable != null) {
				location = locationAbbrVariable.getValue();
			}
		} else if (this.workbook.getStudyDetails().getStudyType() == StudyType.T) {
			// For trials, we look for LOCATION_ABBR(8189) variable at trial instance/environment level.
			MeasurementRow trialInstanceObservations =
					this.workbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(this.instanceNumber));
			if (trialInstanceObservations != null) {
				for (MeasurementData trialInstanceMeasurement : trialInstanceObservations.getDataList()) {
					if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == TermId.LOCATION_ABBR.getId()) {
						location = trialInstanceMeasurement.getValue();
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(location)) {
			LOG.debug(
					"No LOCATION_ABBR(8189) variable was found or it is present but no value is set, in study: {}. [LOCATION] will be defaulted to be null/empty.",
					this.workbook.getStudyDetails().getStudyName());
		}

		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
