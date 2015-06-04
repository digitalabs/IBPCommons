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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class ImportedCrossesList.
 */
public class ImportedCrossesList extends ImportedDescriptionDetails {

	private static final long serialVersionUID = 1L;

	/** The imported crosses. */
	protected List<ImportedCrosses> importedCrosses;

	protected Set<String> errorMessages = new HashSet<>();

	/**
	 * Instantiates a new imported crosses list.
	 *
	 * @param filename the filename
	 * @param name the name
	 * @param title the title
	 * @param type the type
	 * @param date the date
	 */
	public ImportedCrossesList(String filename, String name, String title, String type, Date date) {
		super(filename, name, title, type, date);
		this.importedCrosses = new ArrayList<>();
	}

	public ImportedCrossesList() {
		super();
		this.importedCrosses = new ArrayList<>();

	}

	/**
	 * Gets the imported crosses
	 *
	 * @return the imported crosses
	 */
	public List<ImportedCrosses> getImportedCrosses() {
		return this.importedCrosses;
	}

	/**
	 * Sets the imported crosses
	 *
	 * @param importedCrosses the new imported crosses
	 */
	public void setImportedGermplasms(List<ImportedCrosses> importedCrosses) {
		this.importedCrosses = importedCrosses;
	}

	/**
	 * Adds the imported crosses
	 *
	 * @param importedCrosses the imported crosses
	 */
	public void addImportedCrosses(ImportedCrosses importedCrosses) {
		this.importedCrosses.add(importedCrosses);
	}

	public Set<String> getErrorMessages() {
		return this.errorMessages;
	}

	public void addErrorMessages(String errorMsg) {
		this.errorMessages.add(errorMsg);
	}

	public boolean hasPlotDuplicate() {
		if (this.importedCrosses != null) {
			for (ImportedCrosses crosses : this.importedCrosses) {
				if (crosses.isPlotDupe()) {
					return true;
				}
			}
		}
		return false;
	}
}
