
package org.generationcp.commons.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CrossingUtilTest {

	private final Integer methodId = 99;
	private final Integer defaultTypeId = 5;
	private GermplasmDataManager germplasmDataManager;
	private List<Pair<Germplasm, Name>> germplasmPairs;
	private Name name;

	@Before
	public void setUp() {
		this.germplasmDataManager = Mockito.mock(GermplasmDataManager.class);
		germplasmPairs = new ArrayList<>();
		this.name = new Name();
		this.name.setTypeId(this.defaultTypeId);
		Germplasm germplasm = new Germplasm();
		germplasm.setMethodId(this.methodId);
		germplasmPairs.add(new ImmutablePair<Germplasm, Name>(germplasm, name));
	}

	@Test
	public void testApplyMethodNameTypeIfMethodSnametypeExists() throws Exception {
		Method method = new Method();
		method.setSnametype(88);
		Mockito.doReturn(method).when(this.germplasmDataManager).getMethodByID(this.methodId);
		CrossingUtil.applyMethodNameType(this.germplasmDataManager, this.germplasmPairs, this.defaultTypeId);
		Assert.assertEquals("Sname type should be the same as the method snametype", method.getSnametype(), this.name.getTypeId());
	}

	@Test
	public void testApplyMethodNameTypeIfMethodSnametypeDoesNotExists() throws Exception {
		Method method = new Method();
		method.setSnametype(null);
		Mockito.doReturn(method).when(this.germplasmDataManager).getMethodByID(this.methodId);
		CrossingUtil.applyMethodNameType(this.germplasmDataManager, this.germplasmPairs, this.defaultTypeId);
		Assert.assertEquals("Should use the default name type", this.defaultTypeId, this.name.getTypeId());
	}
}
