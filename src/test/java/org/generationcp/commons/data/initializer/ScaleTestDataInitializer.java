
package org.generationcp.commons.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;

public class ScaleTestDataInitializer {

	public static Scale createScale() {
		final Scale scale = new Scale(new Term());
		scale.setDisplayName("SEED_AMOUNT_kg");
		scale.setDefinition("for kg - Weighed");
		return scale;
	}

	public static List<Scale> createScaleList() {
		final List<Scale> scales = new ArrayList<Scale>();
		scales.add(ScaleTestDataInitializer.createScale());
		return scales;
	}
}
