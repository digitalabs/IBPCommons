package org.generationcp.commons.breedingview.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "BreedingViewProject")
@XmlType(propOrder = {"name", "version", "type", "phenotypic"})
public class BreedingViewProject implements Serializable{

    private static final long serialVersionUID = -1125312445342191068L;

    private String name;
    private String version;
    private BreedingViewProjectType type;
    private Phenotypic phenotypic;
    
    @XmlAttribute
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @XmlAttribute
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    @XmlElement(name = "ProjectType")
    public BreedingViewProjectType getType() {
        return type;
    }
    
    public void setType(BreedingViewProjectType type) {
        this.type = type;
    }
    
    @XmlElement(name = "Phenotypic")
    public Phenotypic getPhenotypic() {
        return phenotypic;
    }
    
    public void setPhenotypic(Phenotypic phenotypic) {
        this.phenotypic = phenotypic;
    }
    
}
