package org.generationcp.commons.util;

import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
/**
 * 
 * @author Akhil
 *
 */
public class TransformationUtil {

	private TransformationUtil() {
		
	}

	/**
	 * Warning exception with list on uniques
	 * @param germplasmList
	 * @return
	 */
	public static ImmutableMap<Integer, Germplasm> getGermplasmMap(final List<Germplasm> germplasmList) {
		return Maps.uniqueIndex(germplasmList, new Function<Germplasm, Integer>() {
			public Integer apply(Germplasm from) {
				return from.getGid(); // or something else
			}
		});
	}
	
	public static ImmutableSet<Integer> getAllGidsFromGermplasmListDataList(final Collection<GermplasmListData> germplasms) {
		final Function<GermplasmListData, Integer> getGidFunction = new Function<GermplasmListData, Integer>() {
			public Integer apply(GermplasmListData germplasmListEntry) {
				return germplasmListEntry.getGid();
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}

	public static ImmutableSet<Integer> getAllGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {
			public Integer apply(Germplasm germplasmListEntry) {
				return germplasmListEntry.getGid();
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}
	
	public static ImmutableSet<Integer> getAllFemaleParentGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {
			public Integer apply(Germplasm germplasmListEntry) {
				return germplasmListEntry.getGpid1();
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}

	public static ImmutableSet<Integer> getAllMaleParentGidsFromGermplasmList(final Collection<Germplasm> germplasms) {
		final Function<Germplasm, Integer> getGidFunction = new Function<Germplasm, Integer>() {
			public Integer apply(Germplasm germplasmListEntry) {
				return germplasmListEntry.getGpid2();
			}
		};
		return FluentIterable.from(germplasms).transform(getGidFunction).toSet();
	}
}
