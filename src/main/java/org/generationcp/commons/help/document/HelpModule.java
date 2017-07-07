
package org.generationcp.commons.help.document;

public enum HelpModule {

	DASHBOARD("dashboard"), PROGRAM_CREATION("program.creation"), MANAGE_LIST(
			"manage.list"), DESIGN_CROSSES("design.crosses"), MANAGE_NURSERIES("manage.nurseries"), MANAGE_TRIALS("manage.trials"), MAKE_LABELS(
			"make.labels"), MAKE_FIELD_MAPS("make.field.maps"), IMPORT_GERMPLASM("import.germplasm"), MANAGE_GENOTYPING_DATA(
			"manage.genotyping.data"), BROWSE_STUDIES("browse.studies"), MANAGE_ONTOLOGIES("manage.ontologies"), HEAD_TO_HEAD(
			"head.to.head"), ADAPTED_GERMPLASM("adapted.germplasm"), TRAIT_DONOR("trait.donor"), SINGLE_SITE_ANALYSIS(
			"single.site.analysis"), MULTI_SITE_ANALYSIS("multi.site.analysis"), MULTI_YEAR_MULTI_SITE_ANALYSIS(
			"multi.year.multi.site.analysis"), BREEDING_VIEW_STANDALONE_FOR_QTL("breeding.view.standalone.for.qtl"), MOLECULAR_BREEDING_DESIGN_TOOL(
			"molecular.breeding.design.tool"), MOLECULAR_BREEDING_DECISION_TOOL("molecular.breeding.decision.tool"), MOLECULAR_BREEDING_PLANNER(
			"molecular.breeding.planner"), MANAGE_PROGRAM_SETTINGS_MANAGE_PROGRAM_SETTING(
			"manage.program.settings.update"), BACKUP_PROGRAM_DATA("backup.program.data"), RESTORE_PROGRAM_DATA(
			"restore.program.data"), DATA_IMPORT_TOOL("data.import.tool"),MANAGE_LOCATIONS("manage.locations"),MANAGE_BREEDING_METHODS("manage.breeding.methods");

	/* This is the variable name from the property file helplinks.properties */
	private String propertyName;

	HelpModule(String link) {
		this.propertyName = link;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

}
