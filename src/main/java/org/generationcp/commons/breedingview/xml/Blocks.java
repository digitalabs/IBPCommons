package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


public class Blocks implements Serializable{

    private static final long serialVersionUID = 1217154201146887044L;

    private String name;
    
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
