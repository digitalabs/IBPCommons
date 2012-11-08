package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;


public class Fieldbook implements Serializable{

    private static final long serialVersionUID = -4157597335127419264L;

    private String file;
    
    @XmlAttribute
    public String getFile() {
        return file;
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
}
