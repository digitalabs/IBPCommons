/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.commons.parsing.pojo;

import org.generationcp.middleware.pojos.ListDataProject;

import java.io.Serializable;

/**
 * The Class ImportedCrosses.
 */
public class ImportedCrosses extends ImportedGermplasm implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private String notes;
	private String seedsHarvested;
	private String crossingDate;
	private String rawBreedingMethod;

	/**
	 * The female desig.
	 */
	private String femaleDesig;

	/**
	 * The male desig.
	 */
	private String maleDesig;

	/**
	 * The female gid.
	 */
	private String femaleGid;

	/**
	 * The male gid.
	 */
	private String maleGid;

	/**
	 * Instantiates a new imported germplasm.
	 */
	public ImportedCrosses() {

	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryId the entry id
	 * @param desig   the desig
	 * @param check   the check
	 */
	public ImportedCrosses(Integer entryId, String desig, String check) {
		super(entryId, desig, check);
	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryId   the entry id
	 * @param desig     the desig
	 * @param gid       the gid
	 * @param cross     the cross
	 * @param source    the source
	 * @param entryCode the entry code
	 * @param check     the check
	 */
	public ImportedCrosses(Integer entryId, String desig, String gid, String cross
			, String source, String entryCode, String check) {
		super(entryId, desig, gid, cross, source, entryCode, check);
	}

	/**
	 * Special constructor to create crosses driectly from parsed data
	 *
	 * @param femaleListData
	 * @param maleListData
	 * @param maleStudyName
	 * @param rawBreedingMethod
	 * @param crossingDate
	 * @param seedsHarvested
	 * @param notes
	 * @param entryId
	 */
	public ImportedCrosses(ListDataProject femaleListData, ListDataProject maleListData,
			String femaleStudyName, String maleStudyName, String rawBreedingMethod, String crossingDate,
			String seedsHarvested, String notes, int entryId) {
		this.setFemaleDesig(femaleListData.getDesignation());
		this.setMaleDesig(maleListData.getDesignation());
		this.setFemaleGid(femaleListData.getGermplasmId().toString());
		this.setMaleGid(maleListData.getGermplasmId().toString());

		this.setEntryId(entryId);
		//Parentage: "female designation / male designation"
		this.setCross(femaleListData.getDesignation() + " / " + maleListData.getDesignation());
		// <Source Female Nursery>: "<female Nursery>:<Source Female Entry ID> / <Male Nursery>:<Source Male Entry ID>"
		this.setSource(femaleStudyName + ":" + femaleListData.getEntryId() + " / " + maleStudyName + ":" + maleListData
				.getEntryId());

		this.setEntryCode(String.valueOf(entryId));

		// the optionals
		this.rawBreedingMethod = rawBreedingMethod;
		this.crossingDate = crossingDate;
		this.seedsHarvested = seedsHarvested;
		this.notes = notes;

	}

	public ImportedCrosses(Integer entryId, String desig, String gid, String cross
			, String source, String entryCode, String check, Integer breedingMethodId) {

		super(entryId, desig, gid, cross, source, entryCode, check, breedingMethodId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "ImportedCrosses [entryId=" + getEntryId() + ", desig=" + getDesig()
				+ ", maleDesig=" + maleDesig + ", femaleDesig=" + femaleDesig
				+ ", maleGid=" + maleGid + ", femaleGid=" + femaleGid
				+ ", gid=" + getGid() + ", cross=" + getCross() + ", source=" + getSource()
				+ ", entryCode=" + getEntryCode() + ", check=" + getCheck()
				+ ", breedingMethodId=" + getBreedingMethodId() + ", gpid1=" + getGpid1()
				+ ", gpid2=" + getGpid2() + ", gnpgs=" + getGnpgs() + ", names=" + getNames()
				+ "]";
	}

	public ImportedCrosses copy() {
		ImportedCrosses rec = new ImportedCrosses(getEntryId(),
				getDesig(), getGid(), getCross(), getSource(), getEntryCode(),
				getCheck(), getBreedingMethodId());

		rec.setGpid1(getGpid1());
		rec.setGpid2(getGpid2());
		rec.setGnpgs(getGnpgs());
		rec.setNames(getNames());
		rec.setCheckId(getCheckId());
		rec.setCheckName(getCheckName());
		rec.setIndex(getIndex());
		rec.setFemaleDesig(femaleDesig);
		rec.setMaleDesig(femaleDesig);
		rec.setFemaleGid(femaleGid);
		rec.setMaleGid(femaleGid);

		return rec;
	}

	public String getFemaleDesig() {
		return femaleDesig;
	}

	public void setFemaleDesig(String femaleDesig) {
		this.femaleDesig = femaleDesig;
	}

	public String getMaleDesig() {
		return maleDesig;
	}

	public void setMaleDesig(String maleDesig) {
		this.maleDesig = maleDesig;
	}

	public String getFemaleGid() {
		return femaleGid;
	}

	public void setFemaleGid(String femaleGid) {
		this.femaleGid = femaleGid;
	}

	public String getMaleGid() {
		return maleGid;
	}

	public void setMaleGid(String maleGid) {
		this.maleGid = maleGid;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getSeedsHarvested() {
		return seedsHarvested;
	}

	public void setSeedsHarvested(String seedsHarvested) {
		this.seedsHarvested = seedsHarvested;
	}

	public String getCrossingDate() {
		return crossingDate;
	}

	public void setCrossingDate(String crossingDate) {
		this.crossingDate = crossingDate;
	}

	/**
	 * Retrieves the breeding method of this cross.
	 * Note that this breeding method is unprocessed, its possible that this might not exists in the methods table at all
	 * to process the method, retrive the method from the database and set this.breedingMethodId
	 *
	 * @return
	 */
	public String getRawBreedingMethod() {
		return rawBreedingMethod;
	}

	public void setRawBreedingMethod(String rawBreedingMethod) {
		this.rawBreedingMethod = rawBreedingMethod;
	}
}
