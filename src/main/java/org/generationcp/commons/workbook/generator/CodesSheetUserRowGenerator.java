
package org.generationcp.commons.workbook.generator;

import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.parsing.ExcelCellStyleBuilder;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CodesSheetUserRowGenerator extends CodesSheetRowGenerator<WorkbenchUser> {

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private UserService userService;

	@Override
	List<WorkbenchUser> getSourceItem() {
		final Project project = this.contextUtil.getProjectInContext();
		return this.userService.getUsersByProjectId(project.getProjectId());
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
	String getFcode(final WorkbenchUser user) {
		return user.getUserid().toString();
	}

	@Override
	String getFname(final WorkbenchUser user) {
		final Person person = this.userService.getPersonById(user.getPerson().getId());
		return person.getDisplayName();
	}
}
