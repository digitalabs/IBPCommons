package org.generationcp.commons.service.impl;

import java.text.SimpleDateFormat;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Revolves Season value for Nurseries and Trials.
 */
public class SeasonResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(SeasonResolver.class);

	public SeasonResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, Workbook workbook,
			String instanceNumber) {
		super(ontologyVariableDataManager, contextUtil, workbook, instanceNumber);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.SEASON_VAR;
	}

	@Override
	protected String getDefaultValue() {
		// Default the season to current year and month.
		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());
		LOG.debug(
				"No Crop_season_Code(8371) variable was found or it is present but no value is set, in study: {}. Defaulting [SEASON] with: {}.",
				this.workbook.getStudyDetails().getStudyName(), currentYearAndMonth);
		return currentYearAndMonth;
	}
}
