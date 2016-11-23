
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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationResolver implements KeyComponentValueResolver {

	protected List<MeasurementVariable> conditions;
	protected MeasurementRow trailInstanceObservation;
	protected StudyType studyType;

	private static final Logger LOG = LoggerFactory.getLogger(LocationResolver.class);

	public LocationResolver(final List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyType studyType) {
		this.studyType = studyType;
		this.trailInstanceObservation = trailInstanceObservation;
		this.conditions = conditions;
	}

	@Override
	public String resolve() {
		String location = "";

		if (this.studyType == StudyType.N) {

			if (this.conditions != null) {
				final ImmutableMap<Integer, MeasurementVariable> conditionsMap =
						Maps.uniqueIndex(this.conditions, new Function<MeasurementVariable, Integer>() {

							@Override
							public Integer apply(final MeasurementVariable measurementVariable) {
								return measurementVariable.getTermId();
							}
						});

				if (conditionsMap.containsKey(TermId.LOCATION_ABBR.getId())) {
					location = conditionsMap.get(TermId.LOCATION_ABBR.getId()).getValue();
				} else if (conditionsMap.containsKey(TermId.TRIAL_LOCATION.getId())) {
					location = conditionsMap.get(TermId.TRIAL_LOCATION.getId()).getValue();
				} else {
					location = conditionsMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
				}
			}

		} else if (this.studyType == StudyType.T) {

			if (this.trailInstanceObservation != null) {
				final ImmutableMap<Integer, MeasurementData> dataListMap =
						Maps.uniqueIndex(this.trailInstanceObservation.getDataList(), new Function<MeasurementData, Integer>() {

							@Override
							public Integer apply(final MeasurementData measurementData) {
								return measurementData.getMeasurementVariable().getTermId();
							}
						});

				if (dataListMap.containsKey(TermId.LOCATION_ABBR.getId())) {
					location = dataListMap.get(TermId.LOCATION_ABBR.getId()).getValue();
				} else if (dataListMap.containsKey(TermId.TRIAL_LOCATION.getId())) {
					location = dataListMap.get(TermId.TRIAL_LOCATION.getId()).getValue();
				} else {
					location = dataListMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
				}
			}
		}

		if (StringUtils.isBlank(location)) {
			LocationResolver.LOG
					.debug("No LOCATION_ABBR(8189), LOCATION_NAME(8180) or TRIAL_INSTANCE(8170) variable was found in " + this.studyType.getLabel() + ". Or it is present but no value is set. "
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
