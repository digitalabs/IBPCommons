package org.generationcp.commons.service.impl;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectPrefixResolver extends CategoricalKeyCodeResolverBase {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectPrefixResolver.class);

	public ProjectPrefixResolver(OntologyVariableDataManager ontologyVariableDataManager, ContextUtil contextUtil, Workbook workbook,
			String instanceNumber) {
		super(ontologyVariableDataManager, contextUtil, workbook, instanceNumber);
	}

	@Override
	protected TermId getKeyCodeId() {
		return TermId.PROJECT_PREFIX;
	}

	@Override
	protected String getDefaultValue() {
		LOG.debug(
				"No Project_Prefix(3001) variable was found or it is present but no value is set, in study: {}."
						+ "Resolving Program value to be an empty string.",
				this.workbook.getStudyDetails().getStudyName());
		return "";
	}
}
