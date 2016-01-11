package org.generationcp.commons.service;

import org.generationcp.middleware.domain.oms.StudyType;

public class GermplasmOriginGenerationParameters {

	private String crop;
	private String studyName;
	private String maleStudyName;
	private String femaleStudyName;
	private StudyType studyType;
	private String location;
	private String season;
	private String plotNumber;
	private String malePlotNumber;
	private String femalePlotNumber;
	private boolean isCross = false;
	private String selectionNumber;

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

	
	public String getFemaleStudyName() {
		return femaleStudyName;
	}
	
	public void setFemaleStudyName(String femaleStudyName) {
		this.femaleStudyName = femaleStudyName;
	}
	
	public String getMaleStudyName() {
		return maleStudyName;
	}
	
	public void setMaleStudyName(String maleStudyName) {
		this.maleStudyName = maleStudyName;
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

	public String getMalePlotNumber() {
		return malePlotNumber;
	}
	
	public void setMalePlotNumber(String malePlotNumber) {
		this.malePlotNumber = malePlotNumber;
	}
	
	public String getFemalePlotNumber() {
		return femalePlotNumber;
	}

	
	public void setFemalePlotNumber(String femalePlotNumber) {
		this.femalePlotNumber = femalePlotNumber;
	}
	
	public boolean isCross() {
		return isCross;
	}
	
	public void setCross(boolean isCross) {
		this.isCross = isCross;
	}
	
	public String getSelectionNumber() {
		return selectionNumber;
	}

	public void setSelectionNumber(String selectionNumber) {
		this.selectionNumber = selectionNumber;
	}
}
