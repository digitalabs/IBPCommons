
package org.generationcp.commons.ruleengine.naming.service;

import java.util.List;

import org.generationcp.commons.pojo.AdvancingSource;

public interface ProcessCodeService {

	List<String> applyProcessCode(String currentInput, String processCode, AdvancingSource source);
}
