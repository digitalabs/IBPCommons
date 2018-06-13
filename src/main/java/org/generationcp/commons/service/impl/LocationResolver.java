
package org.generationcp.commons.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationResolver implements KeyComponentValueResolver {

	protected List<MeasurementVariable> conditions;
	protected MeasurementRow trailInstanceObservation;
	protected StudyTypeDto studyType;
	protected Map<String, String> locationIdNameMap;

	private static final Logger LOG = LoggerFactory.getLogger(LocationResolver.class);

	public LocationResolver(final List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyTypeDto studyType, final Map<String, String> locationIdNameMap) {
		this.studyType = studyType;
		this.trailInstanceObservation = trailInstanceObservation;
		this.conditions = conditions;
		this.locationIdNameMap = locationIdNameMap;
	}

	@Override
	public String resolve() {
		String location = "";

		ImmutableMap<Integer, MeasurementVariable> conditionsMap = null;
		if (this.conditions != null) {
			conditionsMap = Maps.uniqueIndex(this.conditions, new Function<MeasurementVariable, Integer>() {

				@Override
				public Integer apply(final MeasurementVariable measurementVariable) {
					return measurementVariable.getTermId();
				}
			});
		}

		if (conditionsMap != null) {
			if (conditionsMap.containsKey(TermId.LOCATION_ABBR.getId())) {
				location = conditionsMap.get(TermId.LOCATION_ABBR.getId()).getValue();
			} else if (conditionsMap.containsKey(TermId.TRIAL_LOCATION.getId())) {
				location = conditionsMap.get(TermId.TRIAL_LOCATION.getId()).getValue();
			} else {
				location = conditionsMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
			}
		}

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
			} else if (conditionsMap != null && conditionsMap.containsKey(TermId.LOCATION_ABBR.getId())) {
				location = conditionsMap.get(TermId.LOCATION_ABBR.getId()).getValue();
			} else if (dataListMap.containsKey(TermId.LOCATION_ID.getId())) {
				final String locationId = dataListMap.get(TermId.LOCATION_ID.getId()).getValue();
				location = this.locationIdNameMap.get(locationId);
			}

			if (StringUtils.isBlank(location)) {
				location = dataListMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
			}
		}

		if (StringUtils.isBlank(location)) {
			LocationResolver.LOG.debug(
				"No LOCATION_ABBR(8189), LOCATION_NAME(8180) or TRIAL_INSTANCE(8170) variable was found in " + this.studyType.getLabel()
					+ ". Or it is present but no value is set. " + "Resolving location value to be an empty string.");
			return "";
		}

		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
