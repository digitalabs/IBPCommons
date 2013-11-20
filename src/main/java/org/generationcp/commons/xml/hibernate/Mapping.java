package org.generationcp.commons.xml.hibernate;

import javax.xml.bind.annotation.XmlAttribute;

public class Mapping {

    private String resource;

    @XmlAttribute(name="resource")
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
