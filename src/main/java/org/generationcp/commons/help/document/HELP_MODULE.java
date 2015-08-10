package org.generationcp.commons.help.document;


public enum HELP_MODULE {

	LOGIN_AND_USER_CREATION("www.integratedbreeding.net/158/training/launch-bms"),
	DASHBOARD("www.integratedbreeding.net/73/training/bms-user-manual/manual-page-1"),
	PROGRAM_CREATION("www.integratedbreeding.net/73/training/bms-user-manual/manual-page-1"),
	MANAGE_LIST("www.integratedbreeding.net/160/training/bms-user-manual/manage-lists"),
	MAKE_CROSSES("www.integratedbreeding.net/74/training/bms-user-manual/manual-page-123"),
	MANAGE_NURSERIES("www.integratedbreeding.net/161/training/bms-user-manual/manage-nurseries"),
	MANAGE_TRIALS(""),
	MAKE_LABELS("www.integratedbreeding.net/476/training/bms-user-manual/label-design"),
	MAKE_FIELD_MAPS("www.integratedbreeding.net/477/training/bms-user-manual/make-field-map"),
	IMPORT_GERMPLASM("www.integratedbreeding.net/163/training/bms-user-manual/import-germplasm"),
	MANAGE_GENOTYPING_DATA(""),
	BROWSE_STUDIES("www.integratedbreeding.net/191/training/bms-user-manual/browse-studies"),
	MANAGE_ONTOLOGIES("www.integratedbreeding.net/195/training/manage-ontology"),
	HEAD_TO_HEAD("www.integratedbreeding.net/192/training/bms-user-manual/head-to-head-query"),
	ADAPTED_GERMPLASM("www.integratedbreeding.net/193/training/bms-user-manual/adapted-germplasm-query"),
	TRAIT_DONOR(""),
	SINGLE_SITE_ANALYSIS("www.integratedbreeding.net/429/breeding-management-system/tutorials/maize-single-site-analysis-4-location-batch"),
	MULTI_SITE_ANALYSIS("www.integratedbreeding.net/431/breeding-management-system/tutorials/maize-multi-site-gxe-analysis"),
	MULTI_YEAR_MULTI_SITE_ANALYSIS("www.integratedbreeding.net/269/training/bms-user-manual/multi-site-multi-year-analysis"),
	BREEDING_VIEW_STANDALONE_FOR_QTL("www.integratedbreeding.net/165/training/bms-user-manual/qtl-analysis"),
	MOLECULAR_BREEDING_DESIGN_TOOL("www.integratedbreeding.net/179/training/bms-user-manual/marker-assisted-backcross-breeding-tool"),
	MOLECULAR_BREEDING_DECISION_TOOL("www.integratedbreeding.net/183/breeding-management-system/tutorials/optimas-maize-multiparental-mars"),
	MOLECULAR_BREEDING_PLANNER("www.integratedbreeding.net/222/training/bms-user-manual/molecular-breeding-planner"),
	MANAGE_PROGRAM_SETTINGS_ADD_MANAGE_PROGRAM("www.integratedbreeding.net/73/training/bms-user-manual/manual-page-1"),
	MANAGE_PROGRAM_SETTINGS_MANAGE_PROGRAM_SETTING("www.integratedbreeding.net/170/training/bms-user-manual/program-members"),
	BACKUP_PROGRAM_DATA("www.integratedbreeding.net/478/training/bms-user-manual/backup-breeding-program"),
	RESTORE_PROGRAM_DATA("www.integratedbreeding.net/174/training/backup-and-restore"),
	DATA_IMPORT_TOOL("www.integratedbreeding.net/177/training/data-import-tool");
	
	private String link;
	
	HELP_MODULE(String link){
		this.link = link;
	}
	
	public String getOnLineLink(){
		return "https://".concat(this.link);
	}
	
	public String getOffLineLink(){
		StringBuilder offlineLink = new StringBuilder();
		offlineLink.append("BMS_HTML/");
		offlineLink.append(this.link);
		offlineLink.append(".html");
		
		return offlineLink.toString();
	}
	
}
