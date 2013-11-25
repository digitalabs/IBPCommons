package org.generationcp.commons.sea.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.generationcp.commons.breedingview.xml.Trait;

@XmlRootElement(name = "Traits")
@XmlType(propOrder = {"traits"})
public class Traits implements Serializable {
	
	private static final long serialVersionUID = 1L;
	

	private List<Trait> traits;


	@XmlElement(name = "Trait")
	public List<Trait> getTraits() {
		return traits;
	}


	public void setTraits(List<Trait> traits) {
		this.traits = traits;
	}
	
	public void add(Trait trait){
		
		if (traits == null){
			traits = new ArrayList<Trait>(); 
			traits.add(trait);
		}else{
			traits.add(trait);
		}
		
		
	}


	
}