package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"traits", "genotypes", "blocks", "replicates", "fieldbook"})
public class Phenotypic implements Serializable{

    private static final long serialVersionUID = 4607799676192587392L;

    private List<Trait> traits;
    private Genotypes genotypes;
    private Blocks blocks;
    private Replicates replicates;
    private Fieldbook fieldbook;
    
    @XmlElement(name = "Trait")
    public List<Trait> getTraits() {
        return traits;
    }
    
    public void setTraits(List<Trait> traits) {
        this.traits = traits;
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
    
    @XmlElement(name = "Fieldbook")
    public Fieldbook getFieldbook() {
        return fieldbook;
    }
    
    public void setFieldbook(Fieldbook fieldbook) {
        this.fieldbook = fieldbook;
    }
    
}
