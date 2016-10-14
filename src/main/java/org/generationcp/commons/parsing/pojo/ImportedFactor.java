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
 * The Class ImportedFactor.
 */
public class ImportedFactor implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The name. */
	private String name;

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

	/** The nested in. */
	private String nestedIn;

	/** The label. */
	private String label;

	/**
	 * Instantiates a new imported factor.
	 */
	public ImportedFactor() {
	}

	/**
	 * Instantiates a new imported factor.
	 *
	 * @param name the factor name
	 * @param description the description
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @param dataType the data type
	 * @param label the label
	 */
	public ImportedFactor(String name, String description, String property, String scale, String method, String dataType, String label) {
		this.name = name;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.label = label;
	}

	/**
	 * Instantiates a new imported factor.
	 *
	 * @param factor the factor
	 * @param description the description
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @param dataType the data type
	 * @param nestedIn the nested in
	 * @param label the label
	 */
	public ImportedFactor(String factor, String description, String property, String scale, String method, String dataType,
			String nestedIn, String label) {
		this.name = factor;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.dataType = dataType;
		this.nestedIn = nestedIn;
		this.label = label;
	}

	/**
	 * Gets the name.
	 *
	 * @return the factor
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Gets the nested in.
	 *
	 * @return the nested in
	 */
	public String getNestedIn() {
		return this.nestedIn;
	}

	/**
	 * Sets the nested in.
	 *
	 * @param nestedIn the new nested in
	 */
	public void setNestedIn(String nestedIn) {
		this.nestedIn = nestedIn;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
