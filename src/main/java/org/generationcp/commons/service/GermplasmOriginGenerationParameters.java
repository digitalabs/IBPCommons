package org.generationcp.commons.service;

import org.generationcp.middleware.domain.oms.StudyType;

public class GermplasmOriginGenerationParameters {

	private String crop;
	private String studyName;
	private StudyType studyType;
	private String location;
	private String season;
	private String plotNumber;

	public String getCrop() {
		return this.crop;
	}

	public void setCrop(String crop) {
		this.crop = crop;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public StudyType getStudyType() {
		return this.studyType;
	}

	public void setStudyType(StudyType studyType) {
		this.studyType = studyType;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getPlotNumber() {
		return this.plotNumber;
	}

	public void setPlotNumber(String plotNumber) {
		this.plotNumber = plotNumber;
	}

}
