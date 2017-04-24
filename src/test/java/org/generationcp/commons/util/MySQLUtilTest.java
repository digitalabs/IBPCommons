package org.generationcp.commons.util;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

	@Mock
	private ContextUtil contextUtil;
	
	@Autowired
	@InjectMocks
	private MySQLUtil mysqlUtil;
	
	@Before
	public void setup() throws SQLException, ClassNotFoundException {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();
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

	@Test
	public void testBuildSequenceTableUpdateQueryString() {

		Assert.assertEquals("UPDATE `sequence` SET `sequence_value`= (SELECT CEIL(MAX(`phenotype_id`)/500)+100 AS sequence_value FROM `phenotype`) WHERE `sequence_name` = 'phenotype';",
				this.mysqlUtil.buildSequenceTableUpdateQueryString("phenotype", "phenotype_id"));
		Assert.assertEquals("UPDATE `sequence` SET `sequence_value`= (SELECT CEIL(MAX(`nd_experiment_phenotype_id`)/500)+100 AS sequence_value FROM `nd_experiment_phenotype`) WHERE `sequence_name` = 'nd_experiment_phenotype';",
				this.mysqlUtil.buildSequenceTableUpdateQueryString("nd_experiment_phenotype", "nd_experiment_phenotype_id"));
		Assert.assertEquals("UPDATE `sequence` SET `sequence_value`= (SELECT CEIL(MAX(`nd_experiment_id`)/500)+100 AS sequence_value FROM `nd_experiment`) WHERE `sequence_name` = 'nd_experiment';",
				this.mysqlUtil.buildSequenceTableUpdateQueryString("nd_experiment", "nd_experiment_id"));

	}
	
	@Test
	public void testUpdateCropTypeAndOwnerOfRestoredPrograms() throws SQLException {
		// Setup mocks
		final Connection connection = Mockito.mock(Connection.class);
		final Statement statement = Mockito.mock(Statement.class);
		Mockito.when(connection.createStatement()).thenReturn(statement);
		
		final int userId = 2;
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(userId);
		final Project testProject = ProjectTestDataInitializer.createProject();
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);
		
		// Method to test
		this.mysqlUtil.updateCropTypeAndCreatorOfRestoredPrograms(connection);
		
		// Verify SQL queries executed
		final ArgumentCaptor<String> queryStringCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(statement, Mockito.times(3)).execute(queryStringCaptor.capture());
		final List<String> queries = queryStringCaptor.getAllValues();
		Assert.assertEquals(3, queries.size());
		Assert.assertEquals("USE workbench", queries.get(0));
		Assert.assertEquals("UPDATE workbench_project set crop_type = '" + testProject.getCropType().getCropName()
								+ "' where user_id = 9999;", queries.get(1));
		Assert.assertEquals("UPDATE workbench_project set user_id = '" + userId + "' where user_id = 9999;", queries.get(2));
	}
	
}
