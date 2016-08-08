
package org.generationcp.commons.spring.util;

import java.util.Objects;

import org.generationcp.commons.context.ContextInfo;

/**
 * Class is essentially a key to enable us to cache local user ids.
 *
 */
public class CropBasedContextInfo {

	private ContextInfo contextInfo;

	private String cropName;

	public CropBasedContextInfo() {
		
	}
	
	public CropBasedContextInfo(ContextInfo contextInfo, String cropName) {
		this.contextInfo = contextInfo;
		this.cropName = cropName;
	}

	public ContextInfo getContextInfo() {
		return contextInfo;
	}

	public void setContextInfo(ContextInfo contextInfo) {
		this.contextInfo = contextInfo;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.contextInfo, this.cropName);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final CropBasedContextInfo other = (CropBasedContextInfo) obj;

		return Objects.equals(this.contextInfo, other.contextInfo) && Objects.equals(this.cropName, other.cropName);
	}

}
