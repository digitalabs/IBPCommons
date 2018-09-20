
package org.generationcp.commons.pojo;

import java.util.HashMap;
import java.util.Map;

public class ExportRow {

	private final Map<Integer, String> valuesMap = new HashMap<>();

	public void addColumnValue(final Integer id, final String value) {
		this.valuesMap.put(id, value);
	}

	public String getValueForColumn(final Integer id) {
		if (this.valuesMap.containsKey(id)) {
			return this.valuesMap.get(id);
		}
		return "";
	}
	
	public Integer size() {
		return this.valuesMap.size();
	}

}
