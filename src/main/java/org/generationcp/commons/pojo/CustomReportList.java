
package org.generationcp.commons.pojo;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "reports")
public class CustomReportList implements Serializable {

	private static final long serialVersionUID = 1L;

	List<CustomReportType> customReportType;
	String profile;

	public CustomReportList() {

	}

	public CustomReportList(List<CustomReportType> customReportType) {
		super();
		this.customReportType = customReportType;
	}

	@XmlElement(name = "report")
	public List<CustomReportType> getCustomReportType() {
		return this.customReportType;
	}

	public void setCustomReportType(List<CustomReportType> customReportType) {
		this.customReportType = customReportType;
	}

	@XmlElement(name = "profile")
	public String getProfile() {
		return this.profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

}
