
package org.generationcp.commons.help.document;

public enum HelpModule {
	// Programs
	DASHBOARD("dashboard"), 
	PROGRAM_CREATION("program.creation"), 
	
	// Breeding Activities
	MANAGE_LIST("manage.list"), 
	DESIGN_CROSSES("design.crosses"), 
	MANAGE_STUDIES("manage.studies"), 
	MAKE_LABELS("make.labels"), 
	MAKE_FIELD_MAPS("make.field.maps"), 
	MANAGE_LOCATIONS("manage.locations"),
	MANAGE_BREEDING_METHODS("manage.breeding.methods"),
	
	// Information Management
	IMPORT_GERMPLASM("import.germplasm"), 
	MANAGE_GENOTYPING_DATA("manage.genotyping.data"), 
	BROWSE_STUDIES("browse.studies"), 
	MANAGE_ONTOLOGIES("manage.ontologies"), 
	DATA_IMPORT_TOOL("data.import.tool"),
	
	// Breeder Queries
	HEAD_TO_HEAD("head.to.head"), 
	ADAPTED_GERMPLASM("adapted.germplasm"), 
	TRAIT_DONOR("trait.donor"), 
	
	// Statistical Analysis
	SINGLE_SITE_ANALYSIS("single.site.analysis"), 
	MULTI_SITE_ANALYSIS("multi.site.analysis"), 
	MULTI_YEAR_MULTI_SITE_ANALYSIS("multi.year.multi.site.analysis"), 
	
	// Program Admin
	MANAGE_PROGRAM_SETTINGS_MANAGE_PROGRAM_SETTING("manage.program.settings.update"), 
	BACKUP_PROGRAM_DATA("backup.program.data"), 
	RESTORE_PROGRAM_DATA("restore.program.data");

	/* This is the variable name from the property file helplinks.properties */
	private String propertyName;

	HelpModule(String link) {
		this.propertyName = link;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

}
