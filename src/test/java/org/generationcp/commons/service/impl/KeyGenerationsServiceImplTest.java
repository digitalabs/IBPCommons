
package org.generationcp.commons.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.service.KeyGenerationService;
import org.generationcp.commons.service.KeyParametersProvider;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.junit.Test;

public class KeyGenerationsServiceImplTest {

	@Test
	public void testKeyGenerationService() {

		KeyGenerationService service = new KeyGenerationServiceImpl();

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

		KeyParametersProvider inMemoryKeyParametersProvider = new KeyParametersProvider() {

			@Override
			public Map<KeyComponent, KeyComponentValueResolver> getKeyParameterResolvers() {
				Map<KeyComponent, KeyComponentValueResolver> paramerters = new HashMap<>();
				paramerters.put(KeyComponent.LOCATION, locationValueResolver);
				paramerters.put(KeyComponent.PLOTNO, plotNumberResolver);
				paramerters.put(KeyComponent.SELECTION_NUMBER, selectionNumberResolver);
				return paramerters;
			}
		};

		String businessKey = service.generateKey(inMemoryKeyTemplateProvider, inMemoryKeyParametersProvider);

		Assert.assertEquals("INDIA-123-456", businessKey);
	}

	// Temporary test to build and test regex
	@Test
	public void testRegexCaptureReplace() {
		String template = "[LOCATION]-[PLOT]-[LOCATION]";
		Matcher matcher = Pattern.compile("(\\[LOCATION\\])").matcher(template);
		System.out.println(matcher.replaceAll("India"));

		// Find distinct plceholders
		String mydata = "[LOCATION]-[PLOTNO]-[LOCATION]-[NAME]-[PLOTNO]";
		Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(mydata);
		Set<String> allMatches = new HashSet<String>();

		while (m.find()) {
			allMatches.add(m.group());
		}
		System.out.println(allMatches);
	}
}
