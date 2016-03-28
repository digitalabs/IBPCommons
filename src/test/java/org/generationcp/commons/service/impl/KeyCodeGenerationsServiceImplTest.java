
package org.generationcp.commons.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.commons.service.KeyCodeGenerationService;
import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.junit.Test;

import junit.framework.Assert;

public class KeyCodeGenerationsServiceImplTest {

	@Test
	public void testGenerateKey() {

		KeyCodeGenerationService service = new KeyCodeGenerationServiceImpl();

		KeyTemplateProvider inMemoryKeyTemplateProvider = new KeyTemplateProvider() {

			@Override
			public String getKeyTemplate() {
				return "[LOCATION]-[PLOTNO][SELECTION_NUMBER]";
			}
		};

		final KeyComponentValueResolver locationValueResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return "INDIA";
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver plotNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return "123";
			}

			@Override
			public boolean isOptional() {
				return false;
			}
		};

		final KeyComponentValueResolver selectionNumberResolver = new KeyComponentValueResolver() {

			@Override
			public String resolve() {
				return "456";
			}

			@Override
			public boolean isOptional() {
				return true;
			}
		};

		Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers = new HashMap<>();
		keyComponentValueResolvers.put(KeyComponent.LOCATION, locationValueResolver);
		keyComponentValueResolvers.put(KeyComponent.PLOTNO, plotNumberResolver);
		keyComponentValueResolvers.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);

		String businessKey = service.generateKey(inMemoryKeyTemplateProvider, keyComponentValueResolvers);

		Assert.assertEquals("INDIA-123-456", businessKey);
	}
}
