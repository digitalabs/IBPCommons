package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


public class Trait implements Serializable{

    private static final long serialVersionUID = 6088046336216001029L;

    private String name;
    private boolean active;
    
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlAttribute
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
