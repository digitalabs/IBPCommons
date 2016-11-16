
package org.generationcp.commons.util;

import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * A utility that helps transform from one collection to another.
 *
 */
public class CollectionTransformationUtil {

	private CollectionTransformationUtil() {

	}

	/**
	 * Helps transform {@link Germplasm} list into a map. Warning am exception is thrown when the list has non unique values
	 * 
	 * @param germplasmList the list of germplasm to transform
	 * @return the resultant map.
	 */
	public static ImmutableMap<Integer, Germplasm> getGermplasmMap(final List<Germplasm> germplasmList) {
		return Maps.uniqueIndex(germplasmList, new Function<Germplasm, Integer>() {

			public Integer apply(Germplasm from) {
				return from.getGid(); // or something else
			}
		});
	}

	/**
	 * Helps retrieve a list of germplasm id from a collection of {@link Germplasm}
	 * 
	 * @param germplasms the list of germplasm to mine
	 * @return the resultant list of germplasm ids.
	 */
	public static ImmutableSet<Integer> getAllGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {

			public Integer apply(Germplasm germplasmListEntry) {
				return germplasmListEntry.getGid();
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}

	/**
	 * Helps retrieve a list of germplasm female parent ids from a collection of {@link Germplasm}
	 * 
	 * @param germplasms the list of germplasm to mine
	 * @return the resultant list of germplasm female parent ids.
	 */
	public static ImmutableSet<Integer> getAllFemaleParentGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {

			public Integer apply(Germplasm germplasmListEntry) {
				final Integer gpid1 = germplasmListEntry.getGpid1();
				if (gpid1 == null) {
					return 0;
				}
				return gpid1;
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}

	/**
	 * Helps retrieve a list of germplasm male parent ids from a collection of {@link Germplasm}
	 * 
	 * @param germplasms the list of germplasm to mine
	 * @return the resultant list of germplasm female parent ids.
	 */
	public static ImmutableSet<Integer> getAllMaleParentGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {

			public Integer apply(Germplasm germplasmListEntry) {
				final Integer gpid2 = germplasmListEntry.getGpid2();
				if (gpid2 == null) {
					return 0;
				}
				return gpid2;
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}
}
