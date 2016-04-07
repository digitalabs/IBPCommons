package org.generationcp.commons.service.impl;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

public class CrossTypeResolver implements KeyComponentValueResolver{

	private static final String singleCross = "Single cross";
	private static final String doubleCross = "Double cross";
	private static final String topCross = "Top Cross";
	private static final String backCross = "Backcross";

	protected StudyType studyType;
	protected Method breedingMethod;
	protected ImportedGermplasm importedGermplasm;
	protected GermplasmDataManager germplasmDataManager;

	public CrossTypeResolver(final StudyType studyType, final Method breedingMethod, final ImportedGermplasm importedGermplasm, GermplasmDataManager germplasmDataManager){
		this.studyType = studyType;
		this.breedingMethod = breedingMethod;
		this.importedGermplasm = importedGermplasm;
		this.germplasmDataManager = germplasmDataManager;
	}

	@Override
	public String resolve() {
		String method = "";

		if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.singleCross)){
			method = "S";
		} else if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.doubleCross)){
			method = "D";
		} else if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.topCross)){
			//TODO There is not method named 'Top Cross' in Database
			method = "T";
		}else if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.backCross)){
			method = getRecurrentParentType(this.importedGermplasm);
		}

		return method;
	}

	private String getRecurrentParentType(final ImportedGermplasm importedGermplasm){

		if(importedGermplasm.getGpid1() == null || importedGermplasm.getGpid2() == null) {
			return "";
		}

		Germplasm femaleParent = this.germplasmDataManager.getGermplasmByGID(importedGermplasm.getGpid1());
		Germplasm maleParent = this.germplasmDataManager.getGermplasmByGID(importedGermplasm.getGpid2());

		if (maleParent.getGnpgs() >= 2
				&& (femaleParent.getGid().equals(maleParent.getGpid1()) || femaleParent.getGid().equals(maleParent.getGpid2()))) {

			return "F";
		} else if (femaleParent.getGnpgs() >= 2
				&& (maleParent.getGid().equals(femaleParent.getGpid1()) || maleParent.getGid().equals(femaleParent.getGpid2()))) {
			return "M";
		}

		return "";
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
