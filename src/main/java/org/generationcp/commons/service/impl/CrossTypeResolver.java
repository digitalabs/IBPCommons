package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;

public class CrossTypeResolver implements KeyComponentValueResolver{

	private static final String singleCross = "Single cross";
	private static final String doubleCross = "Double cross";
	private static final String topCross = "Top Cross";
	private static final String backCross = "Backcross";

	protected StudyType studyType;
	protected Method breedingMethod;
	protected GermplasmDataManager germplasmDataManager;

	public CrossTypeResolver(final StudyType studyType, final Method breedingMethod, GermplasmDataManager germplasmDataManager){
		this.studyType = studyType;
		this.breedingMethod = breedingMethod;
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
			//TODO Determine male or female recurrent
			// Assign M or F
		}

		return method;
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
