
package org.generationcp.commons.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.commons.settings.BreedingMethodSetting;
import org.generationcp.commons.settings.CrossSetting;
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

//FIXME this class should be refactored, methods should be tested with junit tests and made not static
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

		child.setMethodId(
				determineBreedingMethodBasedOnParentalLine(female, male, motherOfFemale, fatherOfFemale, motherOfMale, fatherOfMale));
		return child;
	}

	public static Integer determineBreedingMethodBasedOnParentalLine(final Germplasm female, final Germplasm male,
			final Germplasm motherOfFemale, final Germplasm fatherOfFemale, final Germplasm motherOfMale, final Germplasm fatherOfMale) {
		Integer methodId = null;

		if (female != null && female.getGnpgs() < 0) {
			if (male != null && male.getGnpgs() < 0) {
				methodId = Methods.SINGLE_CROSS.getMethodID();
			} else {
				methodId = determineCrossingMethod(male, female, motherOfMale, fatherOfMale);
			}
		} else {
			if (male != null && male.getGnpgs() < 0) {
				methodId = determineCrossingMethod(female, male, motherOfFemale, fatherOfFemale);
			} else {
				if (female != null && female.getMethodId() == Methods.SINGLE_CROSS.getMethodID() && male != null
						&& male.getMethodId() == Methods.SINGLE_CROSS.getMethodID()) {
					methodId = Methods.DOUBLE_CROSS.getMethodID();
				} else {
					methodId = Methods.COMPLEX_CROSS.getMethodID();
				}
			}
		}

		// we default to using Single Cross as the breeding method in case it doesn't fit in into any of the previous scenarios
		if (methodId == null) {
			methodId = Methods.SINGLE_CROSS.getMethodID();
		}

		return methodId;
	}

	static Integer determineCrossingMethod(Germplasm parent1, Germplasm parent2, Germplasm motherOfParent1,
			Germplasm fatherOfParent1) {

		Integer methodId = null;
		if (parent1 != null && parent1.getGnpgs() == 1) {
			methodId = Methods.SINGLE_CROSS.getMethodID();
		} else if (parent1 != null && parent1.getGnpgs() == 2) {
			if ((motherOfParent1 != null && Objects.equals(motherOfParent1.getGid(), parent2.getGid())) || (fatherOfParent1 != null
					&& Objects.equals(fatherOfParent1.getGid(), parent2.getGid()))) {
				methodId = Methods.BACKCROSS.getMethodID();
			} else {
				methodId = Methods.THREE_WAY_CROSS.getMethodID();
			}
		} else {
			methodId = Methods.COMPLEX_CROSS.getMethodID();
		}

		return methodId;
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
						CrossingUtil.LOG.error(e.getMessage(), e);
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
		return profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT) && CropEnum.WHEAT.toString()
				.equalsIgnoreCase(crop);
	}

	public static boolean isCimmytMaize(final String profile, final String crop) {
		return profile != null && crop != null && profile.equalsIgnoreCase(PedigreeFactory.PROFILE_CIMMYT) && CropEnum.MAIZE.toString()
				.equalsIgnoreCase(crop);
	}
}
