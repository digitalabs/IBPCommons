package org.generationcp.commons.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;

public abstract class CategoricalKeyCodeResolverBase implements KeyComponentValueResolver{

	protected Workbook workbook;
	protected String instanceNumber;

	protected OntologyVariableDataManager ontologyVariableDataManager;
	protected ContextUtil contextUtil;

	public CategoricalKeyCodeResolverBase(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, Workbook workbook,
			String instanceNumber) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
		this.workbook = workbook;
		this.instanceNumber = instanceNumber;
	}

	protected abstract TermId getKeyCodeId();

	protected abstract String getDefaultValue();

	@Override
	public String resolve() {
		String abbreviation = "";

		if (this.workbook.getStudyDetails().getStudyType() == StudyType.N) {
			MeasurementVariable measurementVariable = this.workbook.findConditionById(getKeyCodeId().getId());

			if(measurementVariable != null && StringUtils.isNotBlank(measurementVariable.getValue())){
				Variable variable = this.ontologyVariableDataManager
						.getVariable(this.contextUtil.getCurrentProgramUUID(), measurementVariable.getTermId(), true, false);

				for(TermSummary prefix : variable.getScale().getCategories()){
					if (measurementVariable.getValue().equals(prefix.getId().toString())
							|| measurementVariable.getValue().equals(prefix.getDefinition())) {
						abbreviation = prefix.getDefinition();
						break;
					}
				}
			}
		} else if (this.workbook.getStudyDetails().getStudyType() == StudyType.T) {
			MeasurementRow trialInstanceObservations =
					this.workbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(this.instanceNumber));
			if (trialInstanceObservations != null) {
				for (MeasurementData trialInstanceMeasurement : trialInstanceObservations.getDataList()) {
					if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == getKeyCodeId().getId()) {
						abbreviation = trialInstanceMeasurement.getValue();
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(abbreviation)) {
			abbreviation = getDefaultValue();
		}

		return abbreviation;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
