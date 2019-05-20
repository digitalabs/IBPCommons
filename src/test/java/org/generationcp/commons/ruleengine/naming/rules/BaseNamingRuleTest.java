
package org.generationcp.commons.ruleengine.naming.rules;

import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.commons.pojo.AdvancingSource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/13/2015 Time: 6:02 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:org/generationcp/commons/ruleengine/naming/rules/BaseNamingRuleTest-context.xml"})
public abstract class BaseNamingRuleTest {

	@Resource
	protected ProcessCodeService processCodeService;

	protected AdvancingSource row;

	protected NamingRuleExecutionContext createExecutionContext(List<String> input) {
		return new NamingRuleExecutionContext(null, this.processCodeService, this.row, null, input);
	}
}
