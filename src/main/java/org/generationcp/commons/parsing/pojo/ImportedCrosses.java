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
import java.util.Set;

/**
 * The Class ImportedCrosses.
 */
public class ImportedCrosses extends ImportedGermplasm implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PLOT_DUPE_PREFIX = "Plot Dupe: ";
	public static final String PEDIGREE_DUPE_PREFIX = "Pedigree Dupe: ";
	public static final String PLOT_RECIP_PREFIX = "Plot Recip: ";
	public static final String PEDIGREE_RECIP_PREFIX = "Pedigree Recip: ";
	
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

	private String duplicate;

	private String femalePlotNo;
	
	private String malePlotNo;
	
	private String duplicatePrefix;
	
	private Set<Integer> duplicateEntries;
	
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

	public ImportedCrosses(ListDataProject femaleListData, ListDataProject maleListData,
			String femaleStudyName, String maleStudyName, String femalePlotNo,String malePlotNo,int entryId) {
		this.setFemaleDesig(femaleListData.getDesignation());
		this.setMaleDesig(maleListData.getDesignation());
		this.setFemaleGid(femaleListData.getGermplasmId().toString());
		this.setMaleGid(maleListData.getGermplasmId().toString());
		this.setFemalePlotNo(femalePlotNo);
		this.setMalePlotNo(malePlotNo);
		this.setEntryId(entryId);

		//Parentage: "female designation / male designation"
		this.setCross(femaleListData.getDesignation() + " / " + maleListData.getDesignation());

		// <Source Female Nursery>: "<female Nursery>:<Source Female Plot no> / <Male Nursery>:<Source Male Plot no>"
		this.setSource(
				femaleStudyName + ":" + femalePlotNo + " / " + maleStudyName + ":" + malePlotNo);

	}


	public void setOptionalFields(String rawBreedingMethod, String crossingDate, String seedsHarvested, String notes) {
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

	public String getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}

	public String getFemalePlotNo() {
		return femalePlotNo;
	}

	public void setFemalePlotNo(String femalePlotNo) {
		this.femalePlotNo = femalePlotNo;
	}

	public String getMalePlotNo() {
		return malePlotNo;
	}

	public void setMalePlotNo(String malePlotNo) {
		this.malePlotNo = malePlotNo;
	}
	
	public boolean isPedigreeDupe() {
		return PEDIGREE_DUPE_PREFIX.equals(duplicatePrefix);
	}	

	public boolean isPlotDupe() {
		return PLOT_DUPE_PREFIX.equals(duplicatePrefix);
	}
	
	public boolean isPedigreeRecip() {
		return PEDIGREE_RECIP_PREFIX.equals(duplicatePrefix);
	}
	
	public boolean isPlotRecip() {
		return PLOT_RECIP_PREFIX.equals(duplicatePrefix);
	}

	public Set<Integer> getDuplicateEntries() {
		return duplicateEntries;
	}

	public void setDuplicateEntries(Set<Integer> duplicateEntries) {
		this.duplicateEntries = duplicateEntries;
	}

	public String getDuplicatePrefix() {
		return duplicatePrefix;
	}

	public void setDuplicatePrefix(String duplicatePrefix) {
		this.duplicatePrefix = duplicatePrefix;
	}
}
