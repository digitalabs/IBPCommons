
package org.generationcp.commons.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "report")
public class CustomReportType {

	private String code;
	private String name;

	public CustomReportType() {

	}

	public CustomReportType(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	@XmlElement
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@XmlElement
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
