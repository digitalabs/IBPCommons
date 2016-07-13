package org.generationcp.commons.parsing;

import java.util.List;
import java.util.Map;

/**
 * A generic implementation of the AbstractCsvFileProcessor class that does not perform extra processing on the parsed CSV data
 */
public class GenericCsvFileProcessor extends AbstractCsvFileProcessor<Map<Integer, List<String>>> {
    @Override
    public Map<Integer, List<String>> parseCsvMap(final Map<Integer, List<String>> csvMap) throws FileParsingException {
        return csvMap;
    }
}
