
package org.generationcp.commons.spring.util;

import java.util.Objects;

import org.generationcp.commons.context.ContextInfo;

public class CropBasedContextInfo {

	private final ContextInfo contextInfo;

	private final String cropName;

	public CropBasedContextInfo(ContextInfo contextInfo, String cropName) {
		this.contextInfo = contextInfo;
		this.cropName = cropName;
	}

	public ContextInfo getContextInfo() {
		return this.contextInfo;
	}

	public String getCropName() {
		return this.cropName;
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
