
package org.generationcp.commons.constant;

/**
 * Created by cyrus on 3/5/15.
 */
public enum ToolEnum {
	GERMPLASM_BROWSER("germplasm_browser"), STUDY_BROWSER("study_browser"), STUDY_BROWSER_WITH_ID("study_browser_with_id"), GERMPLASM_LIST_BROWSER(
			"germplasm_list_browser"), GDMS("gdms"), OPTIMAS("optimas"), BREEDING_MANAGER("breeding_manager"), BREEDING_VIEW(
			"breeding_view"), MBDT("mbdt"), LIST_MANAGER("list_manager"), BM_LIST_MANAGER("bm_list_manager"), BM_LIST_MANAGER_MAIN(
			"bm_list_manager_main"), CROSSING_MANAGER("crossing_manager"), NURSERY_TEMPLATE_WIZARD("nursery_template_wizard"), BREEDING_PLANNER(
			"breeding_planner"), IBFB_GERMPLASM_IMPORT("ibfb_germplasm_import"), GERMPLASM_IMPORT("germplasm_import"), HEAD_TO_HEAD_BROWSER(
			"germplasm_headtohead"), MAIN_HEAD_TO_HEAD_BROWSER("germplasm_mainheadtohead"), DATASET_IMPORTER("dataset_importer"), QUERY_FOR_ADAPTED_GERMPLASM(
			"query_for_adapted_germplasm"), TRAIT_DONOR_QUERY("trait_donor_query"), FIELDBOOK_WEB("fieldbook_web"), NURSERY_MANAGER_FIELDBOOK_WEB(
			"nursery_manager_fieldbook_web"), TRIAL_MANAGER_FIELDBOOK_WEB("trial_manager_fieldbook_web"), ONTOLOGY_BROWSER_FIELDBOOK_WEB(
			"ontology_browser_fieldbook_web"), MIGRATOR("migrator");

	String toolName;

	ToolEnum(String toolName) {
		this.toolName = toolName;
	}

	public String getToolName() {
		return this.toolName;
	}

	public static boolean isCorrectTool(String toolName) {
		for (ToolEnum tool : ToolEnum.values()) {
			if (tool.getToolName().equals(toolName)) {
				return true;
			}
		}

		return false;
	}

	public static ToolEnum equivalentToolEnum(String toolName) {
		for (ToolEnum tool : ToolEnum.values()) {
			if (tool.getToolName().equals(toolName)) {
				return tool;
			}
		}

		return null;
	}
}
