
package org.generationcp.commons.util;

import org.generationcp.middleware.pojos.Germplasm;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import junit.framework.Assert;

public class CollectionTransformationUtilTest {

	private static final int GERMPLASM_1_FEMALE_PARENT = 3;

	private static final int GERMPLASM_1_MALE_PARENT = 4;

	private static final int GERMPLASM_2_FEMALE_PARENT = 5;

	private static final int GERMPLASM_2_MALE_PARENT = 6;

	private static final int TEST_GID_1 = 1;

	private static final int TEST_GID_2 = 2;

	private Germplasm germplasm1;

	private Germplasm germplasm2;

	private ImmutableList<Germplasm> germplasmList;

	@Before
	public void setup() {
		germplasm1 = new Germplasm(TEST_GID_1);
		germplasm1.setGpid1(GERMPLASM_1_FEMALE_PARENT);
		germplasm1.setGpid2(GERMPLASM_1_MALE_PARENT);

		germplasm2 = new Germplasm(TEST_GID_2);
		germplasm2.setGpid1(GERMPLASM_2_FEMALE_PARENT);
		germplasm2.setGpid2(GERMPLASM_2_MALE_PARENT);

		germplasmList = ImmutableList.of(germplasm1, germplasm2);

	}

	@Test
	public void testGetGermplasmMap() throws Exception {
		final ImmutableMap<Integer, Germplasm> germplasmMap = CollectionTransformationUtil.getGermplasmMap(germplasmList);
		
		Assert.assertEquals("We should have two results in the map", 2, germplasmMap.size());
		Assert.assertEquals("We execpt id one to contain germplasm1", germplasm1, germplasmMap.get(TEST_GID_1));
		Assert.assertEquals("We execpt id two to contain germplasm1", germplasm2, germplasmMap.get(TEST_GID_2));
	}

	@Test
	public void testGetAllGidsFromGermplasmList() throws Exception {
		final ImmutableSet<Integer> allGidsFromGermplasmList = CollectionTransformationUtil.getAllGidsFromGermplasmList(germplasmList);
		
		Assert.assertEquals("We should have two results in the set returned", 2, allGidsFromGermplasmList.size());
		Assert.assertTrue("We execpt id one to be part of the set", allGidsFromGermplasmList.contains(TEST_GID_1));
		Assert.assertTrue("We execpt id two to be part of the set", allGidsFromGermplasmList.contains(TEST_GID_2));
	}

	@Test
	public void testGetAllFemaleParentGidsFromGermplasmList() throws Exception {
		final ImmutableSet<Integer> femaleGids = CollectionTransformationUtil.getAllFemaleParentGidsFromGermplasmList(germplasmList);

		Assert.assertEquals("We should have two results in the set returned", 2, femaleGids.size());
		Assert.assertTrue("We execpt id 3 to be part of the set", femaleGids.contains(GERMPLASM_1_FEMALE_PARENT));
		Assert.assertTrue("We execpt id 5 to be part of the set", femaleGids.contains(GERMPLASM_2_FEMALE_PARENT));
	}

	@Test
	public void testGetAllMaleParentGidsFromGermplasmList() throws Exception {
		final ImmutableSet<Integer> maleGids = CollectionTransformationUtil.getAllMaleParentGidsFromGermplasmList(germplasmList);
		
		Assert.assertEquals("We should have two results in the set returned", 2, maleGids.size());
		Assert.assertTrue("We execpt id 3 to be part of the set", maleGids.contains(GERMPLASM_1_MALE_PARENT));
		Assert.assertTrue("We execpt id 5 to be part of the set", maleGids.contains(GERMPLASM_2_MALE_PARENT));
	}

}
