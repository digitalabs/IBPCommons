
package org.generationcp.commons.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.pojos.GermplasmList;

public class GermplasmListExportInputValues {

	private String fileName;
	private GermplasmList germplasmList;
	private String ownerName;
	private String exporterName;
	private Integer currentLocalIbdbUserId;
	private Map<String, Boolean> visibleColumnMap;
	private Map<Integer, StandardVariable> columnStandardVariableMap;
	private Map<Integer, StandardVariable> inventoryStandardVariableMap;
	private Map<Integer, StandardVariable> variateStandardVariableMap;
	private Map<Integer, GermplasmParents> germplasmParents;
	private List<? extends GermplasmExportSource> listData;

	public GermplasmListExportInputValues() {
		super();
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public GermplasmList getGermplasmList() {
		return this.germplasmList;
	}

	public void setGermplasmList(GermplasmList germplasmList) {
		this.germplasmList = germplasmList;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getExporterName() {
		return this.exporterName;
	}

	public void setExporterName(String exporterName) {
		this.exporterName = exporterName;
	}

	public Integer getCurrentLocalIbdbUserId() {
		return this.currentLocalIbdbUserId;
	}

	public void setCurrentLocalIbdbUserId(Integer currentLocalIbdbUserId) {
		this.currentLocalIbdbUserId = currentLocalIbdbUserId;
	}

	public Map<String, Boolean> getVisibleColumnMap() {
		return this.visibleColumnMap;
	}

	public void setVisibleColumnMap(Map<String, Boolean> visibleColumnMap) {
		this.visibleColumnMap = visibleColumnMap;
	}

	public Map<Integer, StandardVariable> getColumnStandardVariableMap() {
		if (this.columnStandardVariableMap == null) {
			return new HashMap<>();
		} else {
			return this.columnStandardVariableMap;
		}
	}

	public void setColumnStandardVariableMap(Map<Integer, StandardVariable> columnStandardVariableMap) {
		this.columnStandardVariableMap = columnStandardVariableMap;
	}

	public Map<Integer, GermplasmParents> getGermplasmParents() {
		return this.germplasmParents;
	}

	public void setGermplasmParents(Map<Integer, GermplasmParents> germplasmParents) {
		this.germplasmParents = germplasmParents;
	}

	public Map<Integer, StandardVariable> getInventoryStandardVariableMap() {

		if (this.inventoryStandardVariableMap == null) {
			return new HashMap<>();
		}

		return this.inventoryStandardVariableMap;
	}

	public void setInventoryStandardVariableMap(Map<Integer, StandardVariable> inventoryStandardVariableMap) {
		this.inventoryStandardVariableMap = inventoryStandardVariableMap;
	}

	public Map<Integer, StandardVariable> getVariateStandardVariableMap() {

		if (this.variateStandardVariableMap == null) {
			return new HashMap<>();
		}

		return this.variateStandardVariableMap;
	}

	public void setVariateStandardVariableMap(Map<Integer, StandardVariable> variateStandardVariableMap) {
		this.variateStandardVariableMap = variateStandardVariableMap;
	}

	public List<? extends GermplasmExportSource> getListData() {
		if (this.listData == null) {
			return new ArrayList<>();
		}
		return this.listData;
	}

	public void setListData(List<? extends GermplasmExportSource> listData) {
		this.listData = listData;
	}
}
