package org.generationcp.commons.service.impl;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;

public class CrossTypeResolver implements KeyComponentValueResolver{

	private static final String singleCross = "Single cross";
	private static final String doubleCross = "Double cross";
	private static final String maizeTopCross = "Test cross";
	private static final String wheatTopCross = "Three-way cross";
	private static final String backCross = "Backcross";

	protected StudyType studyType;
	protected Method breedingMethod;
	protected ImportedGermplasm importedGermplasm;
	protected GermplasmDataManager germplasmDataManager;
	protected ContextUtil contextUtil;

	public CrossTypeResolver(final StudyType studyType, final ContextUtil contextUtil, final Method breedingMethod, final ImportedGermplasm importedGermplasm, GermplasmDataManager germplasmDataManager){
		this.studyType = studyType;
		this.breedingMethod = breedingMethod;
		this.importedGermplasm = importedGermplasm;
		this.germplasmDataManager = germplasmDataManager;
		this.contextUtil = contextUtil;
	}

	@Override
	public String resolve() {
		String crossTypeAbbreviation = "";

		String cropName = this.contextUtil.getProjectInContext().getCropType().getCropName();

		if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.singleCross)){
			crossTypeAbbreviation = "S";
		} else if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.doubleCross)){
			crossTypeAbbreviation = "D";
		} else if(this.breedingMethod.getMname().equals(CrossTypeResolver.this.backCross)){
			crossTypeAbbreviation = getRecurrentParentType(this.importedGermplasm);
		} else if(this.isTopCrossMethod(cropName)){
			crossTypeAbbreviation = "T";
		}

		return crossTypeAbbreviation;
	}

	private String getRecurrentParentType(final ImportedGermplasm importedGermplasm){

		Integer gid = Integer.parseInt(importedGermplasm.getGid());

		Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);

		if(germplasm.getGpid1() == null || germplasm.getGpid2() == null) {
			return "";
		}

		Germplasm femaleParent = this.germplasmDataManager.getGermplasmByGID(germplasm.getGpid1());
		Germplasm maleParent = this.germplasmDataManager.getGermplasmByGID(germplasm.getGpid2());

		if (maleParent.getGnpgs() >= 2
				&& (femaleParent.getGid().equals(maleParent.getGpid1()) || femaleParent.getGid().equals(maleParent.getGpid2()))) {

			return "F";
		} else if (femaleParent.getGnpgs() >= 2
				&& (maleParent.getGid().equals(femaleParent.getGpid1()) || maleParent.getGid().equals(femaleParent.getGpid2()))) {
			return "M";
		}

		return "";
	}

	private boolean isTopCrossMethod(final String cropName){
		return cropName.equalsIgnoreCase("wheat") && this.breedingMethod.getMname().equals(CrossTypeResolver.this.wheatTopCross)
				|| cropName.equalsIgnoreCase("maize") && this.breedingMethod.getMname().equals(CrossTypeResolver.this.maizeTopCross);
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
