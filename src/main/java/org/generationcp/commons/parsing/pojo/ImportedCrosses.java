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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.pojos.ListDataProject;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * The Class ImportedCrosses.
 */
public class ImportedCrosses extends ImportedGermplasm implements Serializable {
	
	public static final String MULTIPARENT_BEGIN_CHAR = "[";
	public static final String MULTIPARENT_END_CHAR = "]";

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
	
	private ImportedGermplasmParent femaleParent;
	private List<ImportedGermplasmParent> maleParents = new ArrayList<>();

	private String duplicate;
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

	public ImportedCrosses(ListDataProject femaleListData, List<ListDataProject> maleListData, String femaleStudyName, String maleStudyName,
			String femalePlotNo, String malePlotNo, int entryId) {
		this.setEntryId(entryId);
		this.femaleParent = new ImportedGermplasmParent(femaleListData.getGermplasmId(), femaleListData.getDesignation(), femalePlotNo, femaleStudyName);
	
		for (final ListDataProject data : maleListData) {
			final ImportedGermplasmParent maleParent = new ImportedGermplasmParent(data.getGermplasmId(), data.getDesignation(), malePlotNo, maleStudyName);
			this.maleParents.add(maleParent);
		}
		
		// FIXME : handle for multiple parents
		// Parentage: "female designation / male designation"
		this.setCross(femaleListData.getDesignation() + " / " + maleListData.get(0).getDesignation());
	}
	
	public ImportedCrosses(Integer entryId, String desig, String gid, String cross, String source, String entryCode, String check,
			Integer breedingMethodId) {

		super(entryId, desig, gid, cross, source, entryCode, check, breedingMethodId);
	}

	public void setOptionalFields(String rawBreedingMethod, Integer crossingDate, String notes) {
		this.rawBreedingMethod = rawBreedingMethod;
		this.crossingDate = crossingDate;
		this.notes = notes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImportedCrosses [entryId=" + this.getEntryId() + ", desig=" + this.getDesig() + ", femaleParent=" + this.femaleParent
				+ ", maleParents=" + this.maleParents + ", gid=" + this.getGid() + ", cross=" + this.getCross() + ", source="
				+ this.getSource() + ", entryCode=" + this.getEntryCode() + ", check=" + this.getEntryTypeValue() + ", breedingMethodId="
				+ this.getBreedingMethodId() + ", gpid1=" + this.getGpid1() + ", gpid2=" + this.getGpid2() + ", gnpgs=" + this.getGnpgs()
				+ ", names=" + this.getNames() + "]";
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
		rec.setFemaleParent(this.femaleParent);
		rec.setMaleParents(this.maleParents);
		return rec;
	}

	public String getFemaleDesignation() {
		return this.femaleParent.getDesignation();
	}
	
	public List<String> getMaleDesignations() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, String>() {

			public String apply(ImportedGermplasmParent data) {
				return data.getDesignation();
			}
		}));
	}
	
	private String getMaleParentsValue(final List<String> list) {
		if (list.size() == 1) {
			return list.get(0);
		}
		return MULTIPARENT_BEGIN_CHAR + StringUtils.join(list, ",") + MULTIPARENT_END_CHAR;
	}
	
	public String getMaleDesignationsAsString() {
		return this.getMaleParentsValue(this.getMaleDesignations());
	}

	public String getFemaleGid() {
		return this.femaleParent.getGid().toString();
	}
	
	public List<Integer> getMaleGids() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, Integer>() {

			public Integer apply(ImportedGermplasmParent data) {
				return data.getGid();
			}
		}));
	}
	
	public String getMaleGidsAsString() {
		final List<String> gidsString = new ArrayList<>();
		for (final Integer gid : getMaleGids()) {
			gidsString.add(gid.toString());
		}
		return this.getMaleParentsValue(gidsString);		
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
	 * in the methods table at all to process the method, retrieve the method from the database and set this.breedingMethodId
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

	public Integer getFemalePlotNo() {
		return this.femaleParent.getPlotNo();
	}

	public List<Integer> getMalePlotNos() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, Integer>() {

			public Integer apply(ImportedGermplasmParent data) {
				return data.getPlotNo();
			}
		}));
	}
	
	public List<String> getMalePlotNumbersAsStringList() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, String>() {

			public String apply(ImportedGermplasmParent data) {
				return data.getPlotNo().toString();
			}
		}));
	}
	
	public String getConcatendatedMalePlotNosAsString() {
		return this.getMaleParentsValue(this.getMalePlotNumbersAsStringList());		
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

	public String getFemaleStudyName() {
		return this.femaleParent.getStudyName();
	}
	
	public List<String> getMaleStudyNames() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, String>() {

			public String apply(ImportedGermplasmParent data) {
				return data.getStudyName();
			}
		}));
	}

	public boolean isBreedingMethodInformationAvailable() {
		return ((getBreedingMethodId() != null && getBreedingMethodId() != 0) || !StringUtils.isEmpty(getRawBreedingMethod()));
	}

	public String getFemalePedigree() {
	  return this.femaleParent.getPedigree();
	}
	
	public List<String> getMalePedigree() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, String>() {

			public String apply(ImportedGermplasmParent data) {
				return data.getPedigree();
			}
		}));
	}
	
	public String getMalePedigreeAsString() {
		return this.getMaleParentsValue(this.getMalePedigree());		
	}

	public String getFemaleCross() {
		return this.femaleParent.getCross();
	}
	
	public List<String> getMaleCross() {
		return Lists.newArrayList(Iterables.transform(this.maleParents, new Function<ImportedGermplasmParent, String>() {

			public String apply(ImportedGermplasmParent data) {
				return data.getCross();
			}
		}));
	}

	public ImportedGermplasmParent getFemaleParent() {
		return femaleParent;
	}

	
	public void setFemaleParent(ImportedGermplasmParent femaleParent) {
		this.femaleParent = femaleParent;
	}

	
	public List<ImportedGermplasmParent> getMaleParents() {
		return maleParents;
	}
	
	public void setMaleParents(List<ImportedGermplasmParent> maleParents) {
		this.maleParents = maleParents;
	}
	
	public void setMaleStudyname(final String maleStudyName) {
		for (final ImportedGermplasmParent maleParent : maleParents) {
			maleParent.setStudyName(maleStudyName);
		}
	}
	
	public Boolean isPolyCross() {
		return this.maleParents.size() > 1;
	}
}
