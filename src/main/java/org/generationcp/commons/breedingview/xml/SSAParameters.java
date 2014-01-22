/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.breedingview.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class SSAParameters {
    private String webApiUrl;
    private Long workbenchProjectId;
    private Integer studyId;
    private Integer inputDataSetId;
    private Integer outputDataSetId;
    private String outputDirectory;

    @XmlAttribute(name="WebApiUrl")
    public String getWebApiUrl() {
        return webApiUrl;
    }

    public void setWebApiUrl(String webApiUrl) {
        this.webApiUrl = webApiUrl;
    }

    @XmlAttribute(name="WorkbenchProjectId")
    public Long getWorkbenchProjectId() {
        return workbenchProjectId;
    }

    public void setWorkbenchProjectId(Long workbenchProjectId) {
        this.workbenchProjectId = workbenchProjectId;
    }

    @XmlAttribute(name="StudyId")
    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    @XmlAttribute(name="InputDataSetId")
    public Integer getInputDataSetId() {
        return inputDataSetId;
    }

    public void setInputDataSetId(Integer dataSetId) {
        this.inputDataSetId = dataSetId;
    }

    @XmlAttribute(name="OutputDataSetId")
    public Integer getOutputDataSetId() {
        return outputDataSetId;
    }

    public void setOutputDataSetId(Integer outputDataSetId) {
        this.outputDataSetId = outputDataSetId;
    }

    @XmlAttribute(name="OutputDirectory")
    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
