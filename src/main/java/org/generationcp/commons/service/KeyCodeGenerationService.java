package org.generationcp.commons.service;

import java.util.Map;

public interface KeyCodeGenerationService {

	String generateKey(KeyTemplateProvider keyTemplateProvider, Map<KeyComponent, KeyComponentValueResolver> keyComponentValueResolvers);
}
