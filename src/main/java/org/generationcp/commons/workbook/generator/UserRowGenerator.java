
package org.generationcp.commons.workbook.generator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.stereotype.Component;

@Component
public class UserRowGenerator extends CodesSheetRowGenerator<User> {

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Override
	List<User> getSourceItem() {
		final Project project = this.contextUtil.getProjectInContext();
		return this.workbenchDataManager.getUsersByProjectId(project.getProjectId());
	}

	@Override
	CellStyle getLabelStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.USER_STYLE);
	}

	@Override
	CellStyle getDataStyle() {
		return this.sheetStyles.getCellStyle(ExcelCellStyleBuilder.ExcelCellStyle.TEXT_DATA_FORMAT_STYLE);
	}

	@Override
	String getSection() {
		return RowColumnType.USER.getSection();
	}

	@Override
	String getInfoType() {
		return RowColumnType.USER.toString();
	}

	@Override
	String getFcode(final User user) {
		return user.getUserid().toString();
	}

	@Override
	String getFname(final User user) {
		final Person person = this.workbenchDataManager.getPersonById(user.getUserid());
		return person.getDisplayName();
	}
}
