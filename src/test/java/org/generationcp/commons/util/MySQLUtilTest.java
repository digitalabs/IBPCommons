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
import org.springframework.util.Assert;

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

	@Test
	public void testBackup() throws Exception {
		final File backupFile = this.mysqlUtil
				.backupDatabase("ibdbv2_wheat_merged", this.mysqlUtil.getBackupFilename("ibdbv2_wheat_merged", "" + ".sql", "temp"), true);
		Assert.notNull(backupFile);
		Assert.isTrue(backupFile.getAbsolutePath().contains(".sql"));
	}
}
