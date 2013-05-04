package org.generationcp.commons.breedingview.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class SSAParameters {
    private String webApiUrl;
    private Long workbenchProjectId;
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

    @XmlAttribute(name="OutputDirectory")
    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
}
