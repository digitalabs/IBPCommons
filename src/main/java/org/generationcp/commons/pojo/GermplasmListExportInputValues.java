package org.generationcp.commons.pojo;

import java.util.Map;

import org.generationcp.middleware.pojos.GermplasmList;

public class GermplasmListExportInputValues {
	
	private String fileName;
	private GermplasmList germplasmList;
	private String ownerName;
	private String exporterName;
	private Integer currentLocalIbdbUserId; 
	private Map<String,Boolean> visibleColumnMap;
	
	public GermplasmListExportInputValues() {
		super();
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public GermplasmList getGermplasmList() {
		return germplasmList;
	}
	
	public void setGermplasmList(GermplasmList germplasmList) {
		this.germplasmList = germplasmList;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public String getExporterName() {
		return exporterName;
	}
	
	public void setExporterName(String exporterName) {
		this.exporterName = exporterName;
	}
	
	public Integer getCurrentLocalIbdbUserId() {
		return currentLocalIbdbUserId;
	}
	
	public void setCurrentLocalIbdbUserId(Integer currentLocalIbdbUserId) {
		this.currentLocalIbdbUserId = currentLocalIbdbUserId;
	}
	
	public Map<String, Boolean> getVisibleColumnMap() {
		return visibleColumnMap;
	}
	
	public void setVisibleColumnMap(Map<String, Boolean> visibleColumnMap) {
		this.visibleColumnMap = visibleColumnMap;
	}
}
