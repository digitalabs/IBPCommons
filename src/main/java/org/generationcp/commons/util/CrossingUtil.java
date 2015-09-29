
package org.generationcp.commons.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.commons.settings.BreedingMethodSetting;
import org.generationcp.commons.settings.CrossSetting;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.CropType.CropEnum;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(CrossingUtil.class);

	/**
	 * Determines the proper crossing method for a germplasm based on how its parental lines have been created.
	 *
	 * @param child - the germplasm whose breeding method will be set
	 * @param female - female parent
	 * @param male - male parent
	 * @param motherOfFemale - maternal female grand parent (mommy of female parent)
	 * @param fatherOfFemale - maternal male grand parent (daddy of female parent)
	 * @param motherOfMale - paternal female grand parent (mommy of male parent)
	 * @param fatherOfMale - paternal male grand parent (daddy of male parent)
	 * @return Germplasm - the parameter gc will be returned and its method id should have been set correctly
	 * @throws MiddlewareQueryException
	 */
	public static Germplasm setCrossingBreedingMethod(final Germplasm child, final Germplasm female, final Germplasm male,
			final Germplasm motherOfFemale, final Germplasm fatherOfFemale, final Germplasm motherOfMale, final Germplasm fatherOfMale) {

		if (female != null && female.getGnpgs() < 0) {
			if (male != null && male.getGnpgs() < 0) {
				child.setMethodId(101);
			} else {
				if (male != null && male.getGnpgs() == 1) {
					child.setMethodId(101);
				} else if (male != null && male.getGnpgs() == 2) {
					if (motherOfMale != null && motherOfMale.getGid() == female.getGid() || fatherOfMale != null
							&& fatherOfMale.getGid() == female.getGid()) {
						child.setMethodId(107);
					} else {
						child.setMethodId(102);
					}
				} else {
					child.setMethodId(106);
				}
			}
		} else {
			if (male != null && male.getGnpgs() < 0) {
				if (female != null && female.getGnpgs() == 1) {
					child.setMethodId(101);
				} else if (female != null && female.getGnpgs() == 2) {
					if (motherOfFemale != null && motherOfFemale.getGid() == male.getGid() || fatherOfFemale != null
							&& fatherOfFemale.getGid() == male.getGid()) {
						child.setMethodId(107);
					} else {
						child.setMethodId(102);
					}
				} else {
					child.setMethodId(106);
				}
			} else {
				if (female != null && female.getMethodId() == 101 && male != null && male.getMethodId() == 101) {
					child.setMethodId(103);
				} else {
					child.setMethodId(106);
				}
			}
		}

		if (child.getMethodId() == null) {
			child.setMethodId(101);
		}
		return child;
	}

	/**
	 * Set breeding method of germplasm based on configuration in setting. Can be same for all crosses or based on status of parental lines
	 *
	 * @return
	 */
	public static boolean applyBreedingMethodSetting(final GermplasmDataManager germplasmDataManager, final CrossSetting setting,
			final List<Germplasm> germplasmList) {

		final BreedingMethodSetting methodSetting = setting.getBreedingMethodSetting();

		// Use same breeding method for all crosses
		if (!methodSetting.isBasedOnStatusOfParentalLines()) {
			CrossingUtil.setBreedingMethodBasedOnMethod(methodSetting.getMethodId(), germplasmList);

			// Use CrossingManagerUtil to set breeding method based on parents
		} else {
			for (final Germplasm germplasm : germplasmList) {

				if (germplasm.getMethodId() == null || germplasm.getMethodId() == 0) {

					final Integer femaleGid = germplasm.getGpid1();
					final Integer maleGid = germplasm.getGpid2();

					try {
						final Germplasm female = germplasmDataManager.getGermplasmByGID(femaleGid);
						final Germplasm male = germplasmDataManager.getGermplasmByGID(maleGid);

						Germplasm motherOfFemale = null;
						Germplasm fatherOfFemale = null;
						if (female != null) {
							motherOfFemale = germplasmDataManager.getGermplasmByGID(female.getGpid1());
							fatherOfFemale = germplasmDataManager.getGermplasmByGID(female.getGpid2());
						}

						Germplasm motherOfMale = null;
						Germplasm fatherOfMale = null;
						if (male != null) {
							motherOfMale = germplasmDataManager.getGermplasmByGID(male.getGpid1());
							fatherOfMale = germplasmDataManager.getGermplasmByGID(male.getGpid2());
						}

						CrossingUtil.setCrossingBreedingMethod(germplasm, female, male, motherOfFemale, fatherOfFemale, motherOfMale,
								fatherOfMale);

					} catch (final MiddlewareQueryException e) {
						CrossingUtil.LOG.error(e.toString() + "\n" + e.getStackTrace());
						return false;
					}
				}
			}
		}
		return true;

	}

	protected static void setBreedingMethodBasedOnMethod(final Integer breedingMethodId, final List<Germplasm> germplasmList) {
		for (final Germplasm germplasm : germplasmList) {

			// method id retrieved via the input file is prioritized over a method to be applied to all entries
			if (germplasm.getMethodId() == null || germplasm.getMethodId() == 0) {
				germplasm.setMethodId(breedingMethodId);
			}
		}

	}

	/*
	 * This is supposed to set the correct name type id to name using the crossing method snametype BMS-577
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
		if (profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT)
				&& CropEnum.WHEAT.toString().equalsIgnoreCase(crop)) {
			return true;
		}
		return false;
	}
}
