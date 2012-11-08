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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"type", "design", "envname"})
public class BreedingViewProjectType implements Serializable{

    private static final long serialVersionUID = 5442823266115956331L;
    
    private String type;
    private String design;
    private String envname;
    
    @XmlAttribute
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @XmlAttribute(name = "Design")
    public String getDesign() {
        return design;
    }
    
    public void setDesign(String design) {
        this.design = design;
    }
    
    @XmlAttribute
    public String getEnvname() {
        return envname;
    }
    
    public void setEnvname(String envname) {
        this.envname = envname;
    }
    
}
