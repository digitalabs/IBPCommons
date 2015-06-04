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

package org.generationcp.commons.xml.hibernate;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"propertyList", "mappingList"})
public class SessionFactory {

	private List<Property> propertyList;

	private List<Mapping> mappingList;

	@XmlElement(name = "property", type = Property.class)
	public List<Property> getPropertyList() {
		return this.propertyList;
	}

	public void setPropertyList(List<Property> properties) {
		this.propertyList = properties;
	}

	@XmlElement(name = "mapping", type = Mapping.class)
	public List<Mapping> getMappingList() {
		return this.mappingList;
	}

	public void setMappingList(List<Mapping> mappingList) {
		this.mappingList = mappingList;
	}

	/**
	 * Update the connection URL.
	 * 
	 * @param connectionUrl
	 * @return true if the connection URL has been changed
	 */
	public boolean updateConnectionUrl(String connectionUrl) {
		for (Property property : this.propertyList) {
			if (property.getName().equals("hibernate.connection.url")) {
				String propertyValue = property.getValue();

				boolean sameValue = propertyValue == null ? connectionUrl == null : propertyValue.equals(connectionUrl);
				if (!sameValue) {
					property.setValue(connectionUrl);
				}

				return !sameValue;
			}
		}

		return false;
	}

	/**
	 * Update the database username.
	 * 
	 * @param username
	 * @return true if the username has been changed
	 */
	public boolean updateUsername(String username) {
		for (Property property : this.propertyList) {
			if (property.getName().equals("hibernate.connection.username")) {
				String propertyValue = property.getValue();

				boolean sameValue = propertyValue == null ? username == null : propertyValue.equals(username);
				if (!sameValue) {
					property.setValue(username);
				}

				return !sameValue;
			}
		}

		return false;
	}

	/**
	 * Update the database password.
	 * 
	 * @param password
	 * @return true if the password has been changed
	 */
	public boolean updatePassword(String password) {
		for (Property property : this.propertyList) {
			if (property.getName().equals("hibernate.connection.password")) {
				String propertyValue = property.getValue();

				boolean sameValue = propertyValue == null ? password == null : propertyValue.equals(password);
				if (!sameValue) {
					property.setValue(password);
				}

				return !sameValue;
			}
		}

		return false;
	}
}
