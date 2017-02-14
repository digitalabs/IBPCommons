package org.generationcp.commons.service.impl;

import java.util.Locale;

import org.generationcp.commons.service.CrossNameService;
import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.commons.settings.CrossSetting;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class CrossNameServiceImplTest {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private MessageSource messageSource;

	@InjectMocks
	private CrossNameService crossNameService = new CrossNameServiceImpl();

	private static final String TEST_CROSSING_PREFIX = "PREFIX";

	private CrossSetting crossSetting;

	@Before
	public void setUp() {
		crossSetting = new CrossSetting();
		CrossNameSetting crossNameSetting = new CrossNameSetting();
		crossNameSetting.setPrefix(TEST_CROSSING_PREFIX);
		crossSetting.setCrossNameSetting(crossNameSetting);
	}

	@Test
	public void testGetNextNameInSequence() throws Exception {
		crossSetting.getCrossNameSetting().setStartNumber(3);

		Mockito.when(this.germplasmDataManager.getNextSequenceNumberForCrossName(TEST_CROSSING_PREFIX)).thenReturn("2");
		String nextNameInSequence = this.crossNameService.getNextNameInSequence(this.crossSetting.getCrossNameSetting());
		Assert.assertEquals(TEST_CROSSING_PREFIX+"3", nextNameInSequence);
	}

	@Test
	public void testGetNextNameInSequenceThrowExceptionWhenInvalidStaringNumber() throws Exception {
		crossSetting.getCrossNameSetting().setStartNumber(1);

		Mockito.when(this.germplasmDataManager.getNextSequenceNumberForCrossName(TEST_CROSSING_PREFIX)).thenReturn("2");

		Mockito.when(this.messageSource.getMessage(Mockito.isA(String.class),Mockito.any(Object[].class),Mockito.isA(Locale.class)))
				.thenReturn("The starting sequence number specified will generate conflict with already existing cross codes.");

		try {
			this.crossNameService.getNextNameInSequence(this.crossSetting.getCrossNameSetting());
		} catch (MiddlewareException e) {
			Assert.assertEquals("The starting sequence number specified will generate conflict with already existing cross codes.", e.getMessage());
		}
	}

}
