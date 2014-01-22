/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.sea.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Pipelines")
@XmlType(propOrder = {"pipelines"})
public class Pipelines implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Pipeline> pipelines;

	@XmlElement(name = "Pipeline")
	public List<Pipeline> getPipelines() {
		return pipelines;
	}

	public void setPipelines(List<Pipeline> pipelines) {
		this.pipelines = pipelines;
	}
	
	public void add(Pipeline pipeline){
		
		if (pipelines == null){
			pipelines = new ArrayList<Pipeline>(); 
			pipelines.add(pipeline);
		}else{
			pipelines.add(pipeline);
		}
	}
	
}