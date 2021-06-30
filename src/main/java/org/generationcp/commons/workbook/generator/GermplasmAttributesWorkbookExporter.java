package org.generationcp.commons.workbook.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.util.VariableValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GermplasmAttributesWorkbookExporter extends GermplasmAddedColumnExporter<Variable> {

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	private List<String> addedAttributeColumns = new ArrayList<>();

	@Override
	List<Variable> getSourceItems() {
		final List<Variable> attributeTypeColumns = new ArrayList<>();
		//columnsInfo is null when exporting germplasm list from Study Manager
		if (this.columnsInfo != null) {
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.addVariableType(VariableType.GERMPLASM_PASSPORT);
			variableFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
			final String programUUID = ContextHolder.getCurrentProgram();
			if (StringUtils.isNotEmpty(programUUID)) {
				variableFilter.setProgramUuid(programUUID);
			}
			final List<Variable> attributeTypes = this.ontologyVariableDataManager.getWithFilter(variableFilter);
			final Map<String, Variable>
				attributeTypesMap =
				attributeTypes.stream().collect(Collectors.toMap(u -> u.getName().toUpperCase(), Function.identity(), (u1, u2) -> u1));

			for (final String addedCol : this.columnsInfo.getColumns()) {
				final Variable field = attributeTypesMap.get(addedCol);
				if (field != null) {
					this.addedAttributeColumns.add(field.getName().toUpperCase());
					attributeTypeColumns.add(field);
				}
			}

		}
		return attributeTypeColumns;
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.LABEL_STYLE_VARIATE);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_STYLE);
	}

	@Override
	String getName(final Variable source) {
		return source.getName().toUpperCase();
	}

	@Override
	String getDescription(final Variable source) {
		return source.getDefinition();
	}

	@Override
	String getProperty(final Variable source) {
		return source.getProperty().getName().toUpperCase();
	}

	@Override
	String getScale(final Variable source) {
		return source.getScale().getName().toUpperCase();
	}

	@Override
	String getMethod(final Variable source) {
		return source.getMethod().getName().toUpperCase();
	}

	@Override
	String getValue(final Variable source) {
		return VariableValueUtil.getExpectedRange(source);
	}

	@Override
	String getDatatype(final Variable source) {
		return source.getScale().getDataType().getDataTypeCode();
	}

	@Override
	String getComments(final Variable source) {
		return "";
	}

	@Override
	Boolean doIncludeColumn(final String column) {
		return this.addedAttributeColumns.contains(column);
	}

	public void setAddedAttributeColumns(final List<String> addedAttributeColumns) {
		this.addedAttributeColumns = addedAttributeColumns;
	}

}
