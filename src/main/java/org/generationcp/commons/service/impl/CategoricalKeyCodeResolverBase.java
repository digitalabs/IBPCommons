package org.generationcp.commons.service.impl;

import java.util.List;

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
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

public abstract class CategoricalKeyCodeResolverBase implements KeyComponentValueResolver{

	protected OntologyVariableDataManager ontologyVariableDataManager;
	protected ContextUtil contextUtil;

	protected List<MeasurementVariable> conditions;
	protected MeasurementRow trailInstanceObservation;
	protected StudyType studyType;

	public CategoricalKeyCodeResolverBase(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, List<MeasurementVariable> conditions, final MeasurementRow trailInstanceObservation,
			final StudyType studyType) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
		this.studyType = studyType;
		this.trailInstanceObservation = trailInstanceObservation;
		this.conditions = conditions;
	}

	protected abstract TermId getKeyCodeId();

	protected abstract String getDefaultValue();

	@Override
	public String resolve() {
		String resolvedValue = "";

		if(this.studyType == StudyType.N){
			MeasurementVariable measurementVariable = null;

			if (this.conditions != null) {
				for (MeasurementVariable mv : this.conditions) {
					if (mv.getTermId() == getKeyCodeId().getId()) {
						measurementVariable = mv;
					}
				}
			}

			if(measurementVariable != null && StringUtils.isNotBlank(measurementVariable.getValue())){
				Variable variable = this.ontologyVariableDataManager
						.getVariable(this.contextUtil.getCurrentProgramUUID(), measurementVariable.getTermId(), true, false);

				for(TermSummary prefix : variable.getScale().getCategories()){
					if (measurementVariable.getValue().equals(prefix.getId().toString())
							|| measurementVariable.getValue().equals(prefix.getDefinition())) {
						resolvedValue = prefix.getDefinition();
						break;
					}
				}
			}
		} else if (this.studyType == StudyType.T){
			if (this.trailInstanceObservation != null) {
				for (MeasurementData trialInstanceMeasurement : this.trailInstanceObservation.getDataList()) {
					if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == getKeyCodeId().getId()) {
						resolvedValue = trialInstanceMeasurement.getValue();
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(resolvedValue)) {
			resolvedValue = getDefaultValue();
		}

		return resolvedValue;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
