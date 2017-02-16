package org.generationcp.commons.util;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/testContext.xml" })
public class MySQLUtilTest {
	private static final String DATABASE = "ibdbv2_maize_merged";
	private static final String BACKUP_FILENAME = "C:\\GCP\\Commons\\temp\\ibdbv2_maize_merged_20170209_033042_735_.sql";

	private Project project;

	private ProjectTestDataInitializer projectTestDataInitializer;

	@Autowired
	@InjectMocks
	private MySQLUtil mysqlUtil;

	@Before
	public void setup() throws SQLException, ClassNotFoundException {
		MockitoAnnotations.initMocks(this);
		this.projectTestDataInitializer = new ProjectTestDataInitializer();
		this.project = this.projectTestDataInitializer.createProject();
	}

	@Test
	public void testBuildProjectQueryString() {
		final String projectQueryString = this.mysqlUtil.buildProjectQueryString(this.project).toString();
		final String expectedQueryString = "INSERT into `workbench_project` (`project_id`, `user_id`, `project_name`, `start_date`, `project_uuid`, `crop_type`, `last_open_date`) values (null, 9999, '"
				+ this.project.getProjectName() + "','" + this.project.getStartDate() + "','"
				+ this.project.getUniqueID() + "','tutorial','" + this.project.getLastOpenDate() + "');\n";
		Assert.assertEquals(expectedQueryString, projectQueryString);
	}

	@Test
	public void testbuildCommandStringListIncludeProceduresAndWithPassword() {
		final String password = "password";
		this.mysqlUtil.setPassword(password);
		final List<String> commandStringList = this.mysqlUtil.buildCommandStringList(MySQLUtilTest.DATABASE,
				MySQLUtilTest.BACKUP_FILENAME, true);

		final String mysqlDumpAbsolutePath = new File(this.mysqlUtil.getMysqlDumpPath()).getAbsolutePath();
		;
		final List<String> expectedCommandList = new ArrayList<String>(Arrays.asList(mysqlDumpAbsolutePath,
				"--password=" + password, "--routines", "--complete-insert", "--extended-insert", "--no-create-db",
				"--single-transaction", "--default-character-set=utf8", "--host=" + this.mysqlUtil.getMysqlHost(),
				"--port=" + this.mysqlUtil.getMysqlPort(), "--user=" + this.mysqlUtil.getUsername(),
				MySQLUtilTest.DATABASE, "-r", MySQLUtilTest.BACKUP_FILENAME));

		int i = 0;
		for (final String value : commandStringList) {
			final String expectedValue = expectedCommandList.get(i++);
			Assert.assertEquals("The value should be " + expectedValue, expectedValue, value);
		}
	}

	@Test
	public void testbuildCommandStringListWithPassword() {
		final String password = "password";
		this.mysqlUtil.setPassword(password);
		final List<String> commandStringList = this.mysqlUtil.buildCommandStringList(MySQLUtilTest.DATABASE,
				MySQLUtilTest.BACKUP_FILENAME, false);

		final String mysqlDumpAbsolutePath = new File(this.mysqlUtil.getMysqlDumpPath()).getAbsolutePath();
		;
		final List<String> expectedCommandList = new ArrayList<String>(Arrays.asList(mysqlDumpAbsolutePath,
				"--password=" + password, "--complete-insert", "--extended-insert", "--no-create-db",
				"--single-transaction", "--default-character-set=utf8", "--host=" + this.mysqlUtil.getMysqlHost(),
				"--port=" + this.mysqlUtil.getMysqlPort(), "--user=" + this.mysqlUtil.getUsername(),
				MySQLUtilTest.DATABASE, "-r", MySQLUtilTest.BACKUP_FILENAME));

		int i = 0;
		for (final String value : commandStringList) {
			final String expectedValue = expectedCommandList.get(i++);
			Assert.assertEquals("The value should be " + expectedValue, expectedValue, value);
		}
	}

	@Test
	public void testbuildCommandStringListIncludeProceduresAndNoPassword() {
		this.mysqlUtil.setPassword("");
		final List<String> commandStringList = this.mysqlUtil.buildCommandStringList(MySQLUtilTest.DATABASE,
				MySQLUtilTest.BACKUP_FILENAME, true);

		final String mysqlDumpAbsolutePath = new File(this.mysqlUtil.getMysqlDumpPath()).getAbsolutePath();
		final List<String> expectedCommandList = new ArrayList<String>(Arrays.asList(mysqlDumpAbsolutePath,
				"--routines", "--complete-insert", "--extended-insert", "--no-create-db", "--single-transaction",
				"--default-character-set=utf8", "--host=" + this.mysqlUtil.getMysqlHost(),
				"--port=" + this.mysqlUtil.getMysqlPort(), "--user=" + this.mysqlUtil.getUsername(),
				MySQLUtilTest.DATABASE, "-r", MySQLUtilTest.BACKUP_FILENAME));

		int i = 0;
		for (final String value : commandStringList) {
			final String expectedValue = expectedCommandList.get(i++);
			Assert.assertEquals("The value should be " + expectedValue, expectedValue, value);
		}
	}
}
