package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


public class Genotypes implements Serializable{

    private static final long serialVersionUID = 7988900674681004162L;
    
    private String name;
    
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
