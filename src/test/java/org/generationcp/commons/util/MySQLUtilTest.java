package org.generationcp.commons.util;

import java.io.File;
import java.sql.SQLException;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/testContext.xml"})
public class MySQLUtilTest {

	@Mock
	private Project project;

	@Autowired
	@InjectMocks
	private MySQLUtil mysqlUtil;

	@Before
	public void setup() throws SQLException, ClassNotFoundException {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.project).when(this.mysqlUtil.getContextUtil()).getProjectInContext();
		Mockito.doReturn(new CropType("maize")).when(this.project).getCropType();
	}
	
	/*
	 * This is a completely redundant test but it must run or the whole class will fail - THIS TEST NEEDS REWRITING
	 */
	@Test
	public void testBackupDatabase() throws Exception {
		final String backupFilename = this.mysqlUtil.getBackupFilename("database", ".sql", "temp");
		final File backupFile = this.mysqlUtil.backupDatabase("database", backupFilename, true);
		
		Assert.assertNotNull("The backup file should not be null", backupFile);
		Assert.assertTrue("The absolute path should contain '" + backupFilename + "'", backupFile.getAbsolutePath().contains(backupFilename));
		Mockito.verify(this.project, Mockito.times(3)).getCropType();
	}
}
