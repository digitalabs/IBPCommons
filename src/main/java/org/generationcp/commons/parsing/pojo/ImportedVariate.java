/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.parsing.pojo;

import java.io.Serializable;

/**
 * The Class ImportedVariate.
 */
public class ImportedVariate implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The variate. */
	private String variate;

	/** The description. */
	private String description;

	/** The property. */
	private String property;

	/** The scale. */
	private String scale;

	/** The method. */
	private String method;

	/** The data type. */
	private String dataType;

	/** The sample level. */
	private String sampleLevel;

	private boolean isSeedStockVariable;
	private Integer scaleId;

	/**
	 * Instantiates a new imported variate.
	 */
	public ImportedVariate() {

	}

	/**
	 * Instantiates a new imported variate.
	 *
	 * @param variate the variate
	 * @param description the description
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @param dataType the data type
	 */
	public ImportedVariate(String variate, String description, String property, String scale, String method, String dataType) {
		this.variate = variate;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
	}

	/**
	 * Instantiates a new imported variate.
	 *
	 * @param variate the variate
	 * @param description the description
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @param dataType the data type
	 * @param sampleLevel the sample level
	 */
	public ImportedVariate(String variate, String description, String property, String scale, String method, String dataType,
			String sampleLevel) {
		this.variate = variate;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.sampleLevel = sampleLevel;
	}

	/**
	 * Gets the variate.
	 *
	 * @return the variate
	 */
	public String getVariate() {
		return this.variate;
	}

	/**
	 * Sets the variate.
	 *
	 * @param variate the new variate
	 */
	public void setVariate(String variate) {
		this.variate = variate;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * Sets the property.
	 *
	 * @param property the new property
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	public String getScale() {
		return this.scale;
	}

	/**
	 * Sets the scale.
	 *
	 * @param scale the new scale
	 */
	public void setScale(String scale) {
		this.scale = scale;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * Sets the method.
	 *
	 * @param method the new method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public String getDataType() {
		return this.dataType;
	}

	/**
	 * Sets the data type.
	 *
	 * @param dataType the new data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the sample level.
	 *
	 * @return the sample level
	 */
	public String getSampleLevel() {
		return this.sampleLevel;
	}

	/**
	 * Sets the sample level.
	 *
	 * @param sampleLevel the new sample level
	 */
	public void setSampleLevel(String sampleLevel) {
		this.sampleLevel = sampleLevel;
	}

	public boolean isSeedStockVariable() {
		return this.isSeedStockVariable;
	}

	public void setSeedStockVariable(boolean isSeedStockVariable) {
		this.isSeedStockVariable = isSeedStockVariable;
	}

	public Integer getScaleId() {
		return this.scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

}
