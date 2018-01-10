
package org.generationcp.commons.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.interfaces.GermplasmExportSource;
import org.generationcp.middleware.pojos.GermplasmList;

public class GermplasmListExportInputValues {

	private String fileName;
	private GermplasmList germplasmList;
	private String ownerName;
	private String exporterName;
	private Integer currentLocalIbdbUserId;
	private Map<String, Boolean> visibleColumnMap;
	private Map<Integer, Term> columnTermMap;
	private Map<Integer, Variable> inventoryVariableMap;
	private Map<Integer, Variable> variateVariableMap;
	private Map<Integer, GermplasmParents> germplasmParents;
	private List<? extends GermplasmExportSource> listData;

	private GermplasmListNewColumnsInfo currentColumnsInfo;

	public GermplasmListExportInputValues() {
		super();
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public GermplasmList getGermplasmList() {
		return this.germplasmList;
	}

	public void setGermplasmList(final GermplasmList germplasmList) {
		this.germplasmList = germplasmList;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(final String ownerName) {
		this.ownerName = ownerName;
	}

	public String getExporterName() {
		return this.exporterName;
	}

	public void setExporterName(final String exporterName) {
		this.exporterName = exporterName;
	}

	public Integer getCurrentLocalIbdbUserId() {
		return this.currentLocalIbdbUserId;
	}

	public void setCurrentLocalIbdbUserId(final Integer currentLocalIbdbUserId) {
		this.currentLocalIbdbUserId = currentLocalIbdbUserId;
	}

	public Map<String, Boolean> getVisibleColumnMap() {
		return this.visibleColumnMap;
	}

	public void setVisibleColumnMap(final Map<String, Boolean> visibleColumnMap) {
		this.visibleColumnMap = visibleColumnMap;
	}

	public Map<Integer, Term> getColumnTermMap() {
		if (this.columnTermMap == null) {
			return new HashMap<>();
		} else {
			return this.columnTermMap;
		}
	}

	public void setColumnTermMap(final Map<Integer, Term> columnTermMap) {
		this.columnTermMap = columnTermMap;
	}

	public Map<Integer, GermplasmParents> getGermplasmParents() {
		return this.germplasmParents;
	}

	public void setGermplasmParents(final Map<Integer, GermplasmParents> germplasmParents) {
		this.germplasmParents = germplasmParents;
	}

	public Map<Integer, Variable> getInventoryVariableMap() {

		if (this.inventoryVariableMap == null) {
			return new HashMap<>();
		}

		return this.inventoryVariableMap;
	}

	public void setInventoryVariableMap(final Map<Integer, Variable> inventoryStandardVariableMap) {
		this.inventoryVariableMap = inventoryStandardVariableMap;
	}

	public Map<Integer, Variable> getVariateVariableMap() {

		if (this.variateVariableMap == null) {
			return new HashMap<>();
		}

		return this.variateVariableMap;
	}

	public void setVariateVariableMap(final Map<Integer, Variable> variateStandardVariableMap) {
		this.variateVariableMap = variateStandardVariableMap;
	}

	public List<? extends GermplasmExportSource> getListData() {
		if (this.listData == null) {
			return new ArrayList<>();
		}
		return this.listData;
	}

	public void setListData(final List<? extends GermplasmExportSource> listData) {
		this.listData = listData;
	}

	public void setCurrentColumnsInfo(final GermplasmListNewColumnsInfo currentColumnsInfo) {
		this.currentColumnsInfo = currentColumnsInfo;
	}

	public GermplasmListNewColumnsInfo getCurrentColumnsInfo() {
		return this.currentColumnsInfo;
	}
}
