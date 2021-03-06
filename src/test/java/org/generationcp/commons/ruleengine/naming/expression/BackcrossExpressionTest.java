package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.pojo.AdvancingSource;
import junit.framework.Assert;

import org.generationcp.middleware.manager.PedigreeDataManagerImpl;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BackcrossExpressionTest extends TestExpression {

	@Mock
	private PedigreeDataManager pedigreeDataManager;

	@InjectMocks
	private BackcrossExpression expression = new BackcrossExpression();

	@Test
	public void testResolveBackcrossParentFemale() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.FEMALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, "[BC]", null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-F", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentMale() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.MALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, "[BC]", null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-M", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentMaleWithLiteralStringBeforeAndAfterTheProcessCode() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid))
				.thenReturn(PedigreeDataManagerImpl.MALE_RECURRENT);

		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, "AA[BC]CC", null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-AAMCC", values.get(0).toString());

	}

	@Test
	public void testResolveBackcrossParentWithNoRecurringParent() {

		int maleParentGid = 1;
		int femaleParentGid = 2;

		Mockito.when(pedigreeDataManager.calculateRecurrentParent(maleParentGid, femaleParentGid)).thenReturn(PedigreeDataManagerImpl.NONE);

		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", "-", null, "[BC]", null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		source.setFemaleGid(femaleParentGid);
		source.setMaleGid(maleParentGid);

		expression.apply(values, source, null);

		Assert.assertEquals("GERMPLASM_TEST-", values.get(0).toString());

	}


}
