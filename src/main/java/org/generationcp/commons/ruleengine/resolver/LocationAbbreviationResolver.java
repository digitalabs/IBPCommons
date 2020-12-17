package org.generationcp.commons.ruleengine.resolver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Revolves Location value for Nurseries and Trials.
 */
public class LocationAbbreviationResolver implements KeyComponentValueResolver {

	protected List<MeasurementVariable> conditions;
	protected ObservationUnitRow observationUnitRow;
	protected Map<Integer, StudyInstance> studyInstanceMap;

	private static final Logger LOG = LoggerFactory.getLogger(LocationAbbreviationResolver.class);

	public LocationAbbreviationResolver(final ObservationUnitRow observationUnitRow, final Map<Integer, StudyInstance> studyInstanceMap) {

		this.observationUnitRow = observationUnitRow;
		this.studyInstanceMap = studyInstanceMap;
	}

	@Override
	public String resolve() {
		String location = "";

		if (this.observationUnitRow != null) {
			final ImmutableMap<Integer, ObservationUnitData> dataListMap =
				Maps.uniqueIndex(this.observationUnitRow.getVariables().values(), new Function<ObservationUnitData, Integer>() {

					@Override
					public Integer apply(final ObservationUnitData observationUnitData) {
						return observationUnitData.getVariableId();
					}
				});

			final String instanceNo = dataListMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()).getValue();
			if (instanceNo != null && studyInstanceMap != null && studyInstanceMap.containsKey(Integer.valueOf(instanceNo))) {
				location = studyInstanceMap.get(Integer.valueOf(instanceNo)).getLocationAbbreviation();
			}

		} else {
			if (studyInstanceMap != null && !studyInstanceMap.isEmpty()) {
				final StudyInstance firstInstance = studyInstanceMap.get(1);
				if (firstInstance != null) {
					location = firstInstance.getLocationAbbreviation();
				}
			}
		}

		if (StringUtils.isBlank(location)) {
			LocationAbbreviationResolver.LOG.debug(
				"No LOCATION_ABBR(8189), LOCATION_NAME(8180) or TRIAL_INSTANCE(8170) variable was found in the study. "
					+ "Or it is present but no value is set. Resolving location value to be an empty string.");
			return "";
		}

		return location;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
