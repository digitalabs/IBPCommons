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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.ListDataProject;

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

	public static final String SEED_SOURCE_PENDING = "Pending";

	private String notes;
	private Integer crossingDate;
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

  	private String femalePedigree;

  	private String malePedigree;

	private String duplicate;

	private String femalePlotNo;

	private String malePlotNo;

	private String duplicatePrefix;

	private String maleStudyName;
	
	private String femaleStudyName;
	
	private Set<Integer> duplicateEntries;

    private String femaleCross;

  	private String maleCross;

	/**
	 * Instantiates a new imported germplasm.
	 */
	public ImportedCrosses() {

	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryId the entry id
	 * @param desig the desig
	 * @param check the check
	 */
	public ImportedCrosses(Integer entryId, String desig, String check) {
		super(entryId, desig, check);
	}

	/**
	 * Instantiates a new imported germplasm.
	 *
	 * @param entryId the entry id
	 * @param desig the desig
	 * @param gid the gid
	 * @param cross the cross
	 * @param source the source
	 * @param entryCode the entry code
	 * @param check the check
	 */
	public ImportedCrosses(Integer entryId, String desig, String gid, String cross, String source, String entryCode, String check) {
		super(entryId, desig, gid, cross, source, entryCode, check);
	}

	public ImportedCrosses(ListDataProject femaleListData, ListDataProject maleListData, String femaleStudyName, String maleStudyName,
			String femalePlotNo, String malePlotNo, int entryId) {
		this.setFemaleDesig(femaleListData.getDesignation());
		this.setMaleDesig(maleListData.getDesignation());
		this.setFemaleGid(femaleListData.getGermplasmId().toString());
		this.setMaleGid(maleListData.getGermplasmId().toString());
		this.setFemalePlotNo(femalePlotNo);
		this.setMalePlotNo(malePlotNo);
		this.setEntryId(entryId);
		this.setMaleStudyName(maleStudyName);
		this.setFemaleStudyName(femaleStudyName);

		// Parentage: "female designation / male designation"
		this.setCross(femaleListData.getDesignation() + " / " + maleListData.getDesignation());
	}

	public void setOptionalFields(String rawBreedingMethod, Integer crossingDate, String notes) {
		this.rawBreedingMethod = rawBreedingMethod;
		this.crossingDate = crossingDate;
		this.notes = notes;
	}

	public ImportedCrosses(Integer entryId, String desig, String gid, String cross, String source, String entryCode, String check,
			Integer breedingMethodId) {

		super(entryId, desig, gid, cross, source, entryCode, check, breedingMethodId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImportedCrosses [entryId=" + this.getEntryId() + ", desig=" + this.getDesig() + ", maleDesig=" + this.maleDesig
				+ ", femaleDesig=" + this.femaleDesig + ", maleGid=" + this.maleGid + ", femaleGid=" + this.femaleGid + ", gid="
				+ this.getGid() + ", cross=" + this.getCross() + ", source=" + this.getSource() + ", entryCode=" + this.getEntryCode()
				+ ", check=" + this.getEntryTypeValue() + ", breedingMethodId=" + this.getBreedingMethodId() + ", gpid1=" + this.getGpid1()
				+ ", gpid2=" + this.getGpid2() + ", gnpgs=" + this.getGnpgs() + ", names=" + this.getNames() + ", malePedigree: "
	  			+ this.getMalePedigree() + " , femalePedigree: " + this.getFemalePedigree() + ", maleCross: , " + this.getMaleCross() +""
				+ ", femaleCross: " + this.getFemaleCross() + "]";
	}

	@Override
	public ImportedCrosses copy() {
		ImportedCrosses rec =
				new ImportedCrosses(this.getEntryId(), this.getDesig(), this.getGid(), this.getCross(), this.getSource(),
						this.getEntryCode(), this.getEntryTypeValue(), this.getBreedingMethodId());

		rec.setGpid1(this.getGpid1());
		rec.setGpid2(this.getGpid2());
		rec.setGnpgs(this.getGnpgs());
		rec.setNames(this.getNames());
		rec.setEntryTypeCategoricalID(this.getEntryTypeCategoricalID());
		rec.setEntryTypeName(this.getEntryTypeName());
		rec.setIndex(this.getIndex());
		rec.setFemaleDesig(this.femaleDesig);
		rec.setMaleDesig(this.femaleDesig);
		rec.setFemaleGid(this.femaleGid);
		rec.setMaleGid(this.femaleGid);
		rec.setFemalePedigree(this.getFemalePedigree());
	  	rec.setMalePedigree(this.getMalePedigree());
		return rec;
	}

	public String getFemaleDesig() {
		return this.femaleDesig;
	}

	public void setFemaleDesig(String femaleDesig) {
		this.femaleDesig = femaleDesig;
	}

	public String getMaleDesig() {
		return this.maleDesig;
	}

	public void setMaleDesig(String maleDesig) {
		this.maleDesig = maleDesig;
	}

	public String getFemaleGid() {
		return this.femaleGid;
	}

	public void setFemaleGid(String femaleGid) {
		this.femaleGid = femaleGid;
	}

	public String getMaleGid() {
		return this.maleGid;
	}

	public void setMaleGid(String maleGid) {
		this.maleGid = maleGid;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getCrossingDate() {
		return this.crossingDate;
	}

	public void setCrossingDate(Integer crossingDate) {
		this.crossingDate = crossingDate;
	}

	/**
	 * Retrieves the breeding method of this cross. Note that this breeding method is unprocessed, its possible that this might not exists
	 * in the methods table at all to process the method, retrive the method from the database and set this.breedingMethodId
	 *
	 * @return
	 */
	public String getRawBreedingMethod() {
		return this.rawBreedingMethod;
	}

	public void setRawBreedingMethod(String rawBreedingMethod) {
		this.rawBreedingMethod = rawBreedingMethod;
	}

	public String getDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}

	public String getFemalePlotNo() {
		return this.femalePlotNo;
	}

	public void setFemalePlotNo(String femalePlotNo) {
		this.femalePlotNo = femalePlotNo;
	}

	public String getMalePlotNo() {
		return this.malePlotNo;
	}

	public void setMalePlotNo(String malePlotNo) {
		this.malePlotNo = malePlotNo;
	}

	public boolean isPedigreeDupe() {
		return ImportedCrosses.PEDIGREE_DUPE_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPlotDupe() {
		return ImportedCrosses.PLOT_DUPE_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPedigreeRecip() {
		return ImportedCrosses.PEDIGREE_RECIP_PREFIX.equals(this.duplicatePrefix);
	}

	public boolean isPlotRecip() {
		return ImportedCrosses.PLOT_RECIP_PREFIX.equals(this.duplicatePrefix);
	}

	public Set<Integer> getDuplicateEntries() {
		return this.duplicateEntries;
	}

	public void setDuplicateEntries(Set<Integer> duplicateEntries) {
		this.duplicateEntries = duplicateEntries;
	}

	public String getDuplicatePrefix() {
		return this.duplicatePrefix;
	}

	public void setDuplicatePrefix(String duplicatePrefix) {
		this.duplicatePrefix = duplicatePrefix;
	}

	public String getMaleStudyName() {
		return this.maleStudyName;
	}

	public void setMaleStudyName(String maleStudyName) {
		this.maleStudyName = maleStudyName;
	}

	public String getFemaleStudyName() {
		return this.femaleStudyName;
	}

	public void setFemaleStudyName(String femaleStudyName) {
		this.femaleStudyName = femaleStudyName;
	}

	public boolean isBreedingMethodInformationAvailable() {
		return ((getBreedingMethodId() != null && getBreedingMethodId() != 0) || !StringUtils.isEmpty(getRawBreedingMethod()));
	}

	public String getFemalePedigree() {
	  return femalePedigree;
	}

	public void setFemalePedigree(final String femalePedigree) {
	  this.femalePedigree = femalePedigree;
	}

	public String getMalePedigree() {
	  return malePedigree;
	}

	public void setMalePedigree(final String malePedigree) {
	  this.malePedigree = malePedigree;
	}

  public String getFemaleCross() {
	return femaleCross;
  }

  public ImportedCrosses setFemaleCross(final String femaleCross) {
	this.femaleCross = femaleCross;
	return this;
  }

  public String getMaleCross() {
	return maleCross;
  }

  public ImportedCrosses setMaleCross(final String maleCross) {
	this.maleCross = maleCross;
	return this;
  }
}
