package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


public class Replicates implements Serializable{

    private static final long serialVersionUID = 5253943424266186757L;

    private String name;

    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
