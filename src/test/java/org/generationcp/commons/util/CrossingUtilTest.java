package org.generationcp.commons.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class CrossingUtilTest {
	private Integer methodId = 99;
	private Integer defaultTypeId = 5;
	private GermplasmDataManagerImpl germplasmDataManager;
	private Map<Germplasm, Name> crossesMap;
	private Name name;
	@Before
	public void setUp(){
		germplasmDataManager = Mockito.spy(new GermplasmDataManagerImpl());
		crossesMap = new HashMap<Germplasm, Name>();
		name = new Name();
		name.setTypeId(defaultTypeId);
		Germplasm germplasm = new Germplasm();
		germplasm.setMethodId(methodId);
		crossesMap.put(germplasm, name);
	}
	
	@Test
	public void testApplyMethodNameTypeIfMethodSnametypeExists() throws Exception{		
		Method method = new Method();
		method.setSnametype(88);		
		Mockito.doReturn(method).when(germplasmDataManager).getMethodByID(methodId);
		CrossingUtil.applyMethodNameType(germplasmDataManager, crossesMap, defaultTypeId);
		Assert.assertEquals("Sname type should be the same as the method snametype", method.getSnametype(), name.getTypeId());
	}
	
	@Test
	public void testApplyMethodNameTypeIfMethodSnametypeDoesNotExists() throws Exception{
		Method method = new Method();
		method.setSnametype(null);		
		Mockito.doReturn(method).when(germplasmDataManager).getMethodByID(methodId);		
		CrossingUtil.applyMethodNameType(germplasmDataManager, crossesMap, defaultTypeId);
		Assert.assertEquals("Should use the default name type", defaultTypeId, name.getTypeId());
	}
}
