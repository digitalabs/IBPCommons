
package org.generationcp.commons.service.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.commons.service.KeyComponent;
import org.generationcp.commons.service.KeyComponentValueResolver;
import org.generationcp.commons.service.KeyGenerationService;
import org.generationcp.commons.service.KeyParametersProvider;
import org.generationcp.commons.service.KeyTemplateProvider;

import com.google.common.base.Strings;

public class KeyGenerationServiceImpl implements KeyGenerationService {


	@Override
	public String generateKey(KeyTemplateProvider keyTemplateProvider, KeyParametersProvider keyParametersProvider) {

		String key = keyTemplateProvider.getKeyTemplate();
		Map<KeyComponent, KeyComponentValueResolver> keyParameters = keyParametersProvider.getKeyParameterResolvers();
		Set<KeyComponent> keySet = keyParameters.keySet();
		Iterator<KeyComponent> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			KeyComponent keyComponent = iterator.next();

			Pattern pattern = Pattern.compile("(\\[" + keyComponent.name() + "\\])");
			Matcher matcher = pattern.matcher(key);

			KeyComponentValueResolver keyComponentValueResolver = keyParameters.get(keyComponent);
			String resolvedValue = keyComponentValueResolver.resolve();

			if (!keyComponentValueResolver.isOptional()) {
				key = matcher.replaceAll(Strings.nullToEmpty(resolvedValue));
			} else {
				if (!Strings.isNullOrEmpty(resolvedValue)) {
					key = matcher.replaceAll("-" + resolvedValue);
				} else {
					key = matcher.replaceAll("");
				}
			}
		}
		return key;
	}

}
