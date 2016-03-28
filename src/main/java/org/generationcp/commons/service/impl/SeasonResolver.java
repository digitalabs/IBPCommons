package org.generationcp.commons.service.impl;

import java.text.SimpleDateFormat;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Revolves Season value for Nurseries and Trials.
 */
public class SeasonResolver implements KeyComponentValueResolver {

	private Workbook workbook;
	private String instanceNumber;

	private OntologyVariableDataManager ontologyVariableDataManager;
	private ContextUtil contextUtil;

	private static final Logger LOG = LoggerFactory.getLogger(SeasonResolver.class);

	public SeasonResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, Workbook workbook,
			String instanceNumber) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.contextUtil = contextUtil;
		this.workbook = workbook;
		this.instanceNumber = instanceNumber;
	}

	@Override
	public String resolve() {
		String season = "";
		if (this.workbook.getStudyDetails().getStudyType() == StudyType.N) {
			// To populate SEASON placeholder in Nurseries, we look for Crop_season_Code(8371) variable in general settings.
			MeasurementVariable seasonVariable = this.workbook.findConditionById(TermId.SEASON_VAR.getId());
			if (seasonVariable != null && StringUtils.isNotBlank(seasonVariable.getValue())) {
				Variable variable =
						this.ontologyVariableDataManager.getVariable(this.contextUtil.getCurrentProgramUUID(), seasonVariable.getTermId(),
								true, false);
				for (TermSummary seasonOption : variable.getScale().getCategories()) {
					// Sometimes the categorical value of season in Workbook is an ID string, sometimes the actual Value/Definition string.
					// Not solved as to why yet. So we deal with it using this ambulance here at the bottom of the cliff :(
					if (seasonVariable.getValue().equals(seasonOption.getId().toString())
							|| seasonVariable.getValue().equals(seasonOption.getDefinition())) {
						season = seasonOption.getDefinition();
						break;
					}
				}
			}
		} else if (this.workbook.getStudyDetails().getStudyType() == StudyType.T) {
			// For trials, we look for Crop_season_Code(8371) variable at trial instance/environment level.
			MeasurementRow trialInstanceObservations =
					this.workbook.getTrialObservationByTrialInstanceNo(Integer.valueOf(this.instanceNumber));
			if (trialInstanceObservations != null) {
				for (MeasurementData trialInstanceMeasurement : trialInstanceObservations.getDataList()) {
					if (trialInstanceMeasurement.getMeasurementVariable().getTermId() == TermId.SEASON_VAR.getId()) {
						season = trialInstanceMeasurement.getValue();
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(season)) {
			// Default the season to current year and month.
			SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
			String currentYearAndMonth = formatter.format(new java.util.Date());
			LOG.debug(
					"No Crop_season_Code(8371) variable was found or it is present but no value is set, in study: {}. Defaulting [SEASON] with: {}.",
					this.workbook.getStudyDetails().getStudyName(), currentYearAndMonth);
			season = currentYearAndMonth;
		}
		return season;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

}
