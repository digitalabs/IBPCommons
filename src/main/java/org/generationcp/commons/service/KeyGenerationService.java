package org.generationcp.commons.service;


public interface KeyGenerationService {

	String generateKey(KeyTemplateProvider keyTemplateProvider, KeyParametersProvider keyParametersProvider);
}
