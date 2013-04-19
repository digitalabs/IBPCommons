/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"traits", "environments", "genotypes", "blocks", "replicates"
        , "rows", "columns", "fieldbook", "webApiUrl", "workbenchProjectId", "outputDirectory"})
public class Phenotypic implements Serializable{

    private static final long serialVersionUID = 4607799676192587392L;

    private List<Trait> traits;
    private Environment environments;
    private Genotypes genotypes;
    private Blocks blocks;
    private Replicates replicates;
    private Rows rows;
    private Columns columns;
    private Fieldbook fieldbook;
    private String webApiUrl;
    private Long workbenchProjectId;
    private String outputDirectory;

    @XmlElement(name = "Trait")
    public List<Trait> getTraits() {
        return traits;
    }
    
    public void setTraits(List<Trait> traits) {
        this.traits = traits;
    }

    @XmlElement(name = "Environments")
    public Environment getEnvironments() {
        return environments;
    }
    
    public void setEnvironments(Environment environments) {
        this.environments = environments;
    }

    @XmlElement(name = "Genotypes")
    public Genotypes getGenotypes() {
        return genotypes;
    }
    
    public void setGenotypes(Genotypes genotypes) {
        this.genotypes = genotypes;
    }
    
    @XmlElement(name = "Blocks")
    public Blocks getBlocks() {
        return blocks;
    }
    
    public void setBlocks(Blocks blocks) {
        this.blocks = blocks;
    }
    
    @XmlElement(name = "Replicates")
    public Replicates getReplicates() {
        return replicates;
    }
    
    public void setReplicates(Replicates replicates) {
        this.replicates = replicates;
    }
    
    @XmlElement(name = "Rows")
    public Rows getRows() {
        return rows;
    }
    
    public void setRows(Rows rows) {
        this.rows = rows;
    }
    
    @XmlElement(name = "Columns")
    public Columns getColumns() {
        return columns;
    }
    
    public void setColumns(Columns columns) {
        this.columns = columns;
    }

    @XmlElement(name = "Fieldbook")
    public Fieldbook getFieldbook() {
        return fieldbook;
    }
    
    public void setFieldbook(Fieldbook fieldbook) {
        this.fieldbook = fieldbook;
    }

    @XmlElement(name = "WebApiUrl")
    public String getWebApiUrl() {
        return webApiUrl;
    }

    public void setWebApiUrl(String webApiUrl) {
        this.webApiUrl = webApiUrl;
    }

    @XmlElement(name = "WorkbenchProjectId")
    public Long getWorkbenchProjectId() {
        return workbenchProjectId;
    }

    public void setWorkbenchProjectId(Long workbenchProjectId) {
        this.workbenchProjectId = workbenchProjectId;
    }

    @XmlElement(name = "OutputDirectory")
    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
