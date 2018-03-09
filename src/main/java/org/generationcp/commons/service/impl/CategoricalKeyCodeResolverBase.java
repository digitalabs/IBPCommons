
package org.generationcp.commons.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

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

		if (Objects.equals(this.studyType.getLabel(), StudyType.N.getLabel())) {
			MeasurementVariable measurementVariable = null;

			if (this.conditions != null) {
				for (final MeasurementVariable mv : this.conditions) {
					if (mv.getTermId() == this.getKeyCodeId().getId()) {
						measurementVariable = mv;
					}
				}
			}

			if (measurementVariable != null && StringUtils.isNotBlank(measurementVariable.getValue())) {
				final Variable variable = this.ontologyVariableDataManager.getVariable(this.contextUtil.getCurrentProgramUUID(),
						measurementVariable.getTermId(), true, false);

				for (final TermSummary prefix : variable.getScale().getCategories()) {
					if (measurementVariable.getValue().equals(prefix.getId().toString())
							|| measurementVariable.getValue().equals(prefix.getDefinition())) {
						resolvedValue = this.isAbbreviationRequired() ? prefix.getName() : prefix.getDefinition();
						break;
					}
				}
			}
		} else if (Objects.equals(this.studyType.getLabel(), StudyType.T.getLabel())) {
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
