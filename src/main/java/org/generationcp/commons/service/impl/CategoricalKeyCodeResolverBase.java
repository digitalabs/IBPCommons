
package org.generationcp.commons.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

import java.util.List;

public abstract class CategoricalKeyCodeResolverBase implements KeyComponentValueResolver {

	protected OntologyVariableDataManager ontologyVariableDataManager;
	protected ContextUtil contextUtil;

	protected List<MeasurementVariable> conditions;
	protected MeasurementRow trailInstanceObservation;
	protected StudyTypeDto studyType;

	public CategoricalKeyCodeResolverBase(final OntologyVariableDataManager ontologyVariableDataManager, final ContextUtil contextUtil,
			final List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation, final StudyTypeDto studyType) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
		this.studyType = studyType;
		this.trailInstanceObservation = trailInstanceObservation;
		this.conditions = conditions;
	}

	protected abstract TermId getKeyCodeId();

	protected abstract boolean isAbbreviationRequired();

	protected abstract String getDefaultValue();

	protected abstract String getValueFromTrialInstanceMeasurementData(MeasurementData measurementData);

	protected abstract String getValueFromTrialConditions(MeasurementVariable trialCondition);

	@Override
	public String resolve() {
		String resolvedValue = "";

		if (this.trailInstanceObservation != null) {
			for (final MeasurementData trialInstanceMeasurement : this.trailInstanceObservation.getDataList()) {
				if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromTrialInstanceMeasurementData(trialInstanceMeasurement);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue) && this.conditions != null) {
			for (final MeasurementVariable trialCondition : this.conditions) {
				if (trialCondition.getTermId() == this.getKeyCodeId().getId()) {
					resolvedValue = this.getValueFromTrialConditions(trialCondition);
					break;
				}
			}
		}
		if (StringUtils.isBlank(resolvedValue)) {
			resolvedValue = this.getDefaultValue();
		}

		return resolvedValue;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
