
package org.generationcp.commons.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Methods;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType.CropEnum;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME this class should be refactored, methods should be tested with junit tests and made not static
public class CrossingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(CrossingUtil.class);

	public static Integer determineBreedingMethodBasedOnParentalLine(final Germplasm female, final Germplasm male,
			final Germplasm motherOfFemale, final Germplasm fatherOfFemale, final Germplasm motherOfMale,
			final Germplasm fatherOfMale) {
		// Single Cross is default breeding method in case it doesn't fit in into any of the previous scenarios
		Integer methodId = Methods.SINGLE_CROSS.getMethodID();

		if (female != null && female.getGnpgs() < 0) {
			if (male == null || male.getGnpgs() < 0) {
				methodId = Methods.SINGLE_CROSS.getMethodID();
			} else {
				methodId = CrossingUtil.determineCrossingMethod(male, female, motherOfMale, fatherOfMale);
			}
		
		// Male is null if it's unknown parent
		} else if (male != null) {
			if (male.getGnpgs() < 0) {
				methodId = CrossingUtil.determineCrossingMethod(female, male, motherOfFemale, fatherOfFemale);
			} else {
				if (female != null && Objects.equals(Methods.SINGLE_CROSS.getMethodID(), female.getMethodId()) && male != null
						&& Objects.equals(Methods.SINGLE_CROSS.getMethodID(), male.getMethodId())) {
					methodId = Methods.DOUBLE_CROSS.getMethodID();
				} else {
					methodId = Methods.COMPLEX_CROSS.getMethodID();
				}
			}
		}

		return methodId;
	}

	static Integer determineCrossingMethod(final Germplasm parent1, final Germplasm parent2,
			final Germplasm motherOfParent1, final Germplasm fatherOfParent1) {

		Integer methodId = null;
		if (parent1 != null && parent1.getGnpgs() == 1) {
			methodId = Methods.SINGLE_CROSS.getMethodID();
		} else if (parent1 != null && parent1.getGnpgs() == 2) {
			if (motherOfParent1 != null && Objects.equals(motherOfParent1.getGid(), parent2.getGid())
					|| fatherOfParent1 != null && Objects.equals(fatherOfParent1.getGid(), parent2.getGid())) {
				methodId = Methods.BACKCROSS.getMethodID();
			} else if (Objects.equals(Methods.SINGLE_CROSS.getMethodID(), parent1.getMethodId())) {
				methodId = Methods.THREE_WAY_CROSS.getMethodID();
			} else {
				methodId = Methods.COMPLEX_CROSS.getMethodID();
			}
		} else {
			methodId = Methods.COMPLEX_CROSS.getMethodID();
		}

		return methodId;
	}

	/*
	 * This is supposed to set the correct name type id to name using the
	 * crossing method snametype BMS-577
	 */
	public static void applyMethodNameType(final GermplasmDataManager germplasmDataManager,
			final List<Pair<Germplasm, Name>> germplasmPairs, final Integer defaultTypeId) {
		final Map<Integer, Method> methodMap = new HashMap<Integer, Method>();
		for (final Pair<Germplasm, Name> pair : germplasmPairs) {
			final Name nameObject = pair.getRight();
			final Germplasm germplasm = pair.getLeft();
			Method method = null;
			if (methodMap.containsKey(germplasm.getMethodId())) {
				method = methodMap.get(germplasm.getMethodId());
			} else {
				try {
					method = germplasmDataManager.getMethodByID(germplasm.getMethodId());
					methodMap.put(germplasm.getMethodId(), method);
				} catch (final MiddlewareQueryException e) {
					CrossingUtil.LOG.error(e.getMessage(), e);
				}
			}
			if (method != null && method.getSnametype() != null) {
				nameObject.setTypeId(method.getSnametype());
			} else {
				// we set the default value
				nameObject.setTypeId(defaultTypeId);
			}
		}

	}

	public static boolean isCimmytWheat(final String profile, final String crop) {
		return profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT)
				&& CropEnum.WHEAT.toString().equalsIgnoreCase(crop);
	}

	public static boolean isCimmytMaize(final String profile, final String crop) {
		return profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT)
				&& CropEnum.MAIZE.toString().equalsIgnoreCase(crop);
	}
}
