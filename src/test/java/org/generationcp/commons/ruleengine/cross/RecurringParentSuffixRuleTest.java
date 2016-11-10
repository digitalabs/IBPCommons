
package org.generationcp.commons.ruleengine.cross;

import junit.framework.Assert;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.settings.CrossSetting;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RecurringParentSuffixRuleTest {

	private static final String TEST_BASE_CROSS_NAME = "Cross";
	private static final int MALE_GID = 100;
	private static final int FEMALE_GID = 101;

	private RecurringParentSuffixRule unitUnderTest;
	private PedigreeDataManager pedigreeDataManager;
	private CrossSetting crossSetting;
	private GermplasmDataManager germplasmDataManager;
	private CrossingRuleExecutionContext ruleContext;

	@Before
	public void setUp() throws Exception {
		unitUnderTest = new RecurringParentSuffixRule();

		pedigreeDataManager = Mockito.mock(PedigreeDataManager.class);
		crossSetting = Mockito.mock(CrossSetting.class);
		germplasmDataManager = Mockito.mock(GermplasmDataManager.class);
		ruleContext = new CrossingRuleExecutionContext(null);
		ruleContext.setPedigreeDataManager(pedigreeDataManager);
		ruleContext.setCurrentCrossName(TEST_BASE_CROSS_NAME);
		ruleContext.setCrossSetting(crossSetting);
		ruleContext.setGermplasmDataManager(germplasmDataManager);
		ruleContext.setMaleGid(MALE_GID);
		ruleContext.setFemaleGid(FEMALE_GID);
	}

	@Test
	public void testRunRuleMaleRecurrent() throws RuleException, MiddlewareQueryException {
		Mockito.when(pedigreeDataManager.calculateRecurrentParent(MALE_GID, FEMALE_GID)).thenReturn(PedigreeDataManager.MALE_RECURRENT);

		String output = (String) unitUnderTest.runRule(ruleContext);
		Assert.assertEquals("Unable to add proper suffix for items with recurrent male parent", TEST_BASE_CROSS_NAME
				+ RecurringParentSuffixRule.MALE_RECURRENT_SUFFIX, output);
	}

	@Test
	public void testRunRuleFemaleRecurrent() throws RuleException, MiddlewareQueryException {
		Mockito.when(pedigreeDataManager.calculateRecurrentParent(MALE_GID, FEMALE_GID)).thenReturn(PedigreeDataManager.FEMALE_RECURRENT);

		String output = (String) unitUnderTest.runRule(ruleContext);
		Assert.assertEquals("Unable to add proper suffix for items with recurrent female parent", TEST_BASE_CROSS_NAME
				+ RecurringParentSuffixRule.FEMALE_RECURRENT_SUFFIX, output);
	}

	@Test
	public void testRunRuleNonRecurrent() throws RuleException, MiddlewareQueryException {
		Mockito.when(pedigreeDataManager.calculateRecurrentParent(MALE_GID, FEMALE_GID)).thenReturn(PedigreeDataManager.NONE);

		String output = (String) unitUnderTest.runRule(ruleContext);
		Assert.assertEquals("Wrong output for items with no recurrent parent", TEST_BASE_CROSS_NAME, output);
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(RecurringParentSuffixRule.KEY, unitUnderTest.getKey());
	}

	@Test
	public void testGetProcessCode() {
		Assert.assertEquals(RecurringParentSuffixRule.PROCESS_CODE, unitUnderTest.getProcessCode());
	}
}
