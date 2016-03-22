package org.generationcp.commons.service;

import java.util.Map;


public interface KeyParametersProvider {

	Map<KeyComponent, KeyComponentValueResolver> getKeyParameterResolvers();
}
