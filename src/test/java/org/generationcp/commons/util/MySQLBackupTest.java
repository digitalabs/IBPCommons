/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.util;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class MySQLBackupTest {

	private static MySQLUtil mysqlBackup;

	private static File backupFile;

	@BeforeClass
	public static void setup() throws SQLException, ClassNotFoundException {
		MySQLBackupTest.mysqlBackup = new MySQLUtil();
		MySQLBackupTest.mysqlBackup.setMysqlDumpPath("C:/IBWorkflowSystem/infrastructure/mysql/bin/mysqldump.exe");
		MySQLBackupTest.mysqlBackup.setBackupDir(".");
		MySQLBackupTest.mysqlBackup.setMysqlDriver("com.mysql.jdbc.Driver");
		MySQLBackupTest.mysqlBackup.setMysqlHost("localhost");
		MySQLBackupTest.mysqlBackup.setMysqlPort(13306);
		MySQLBackupTest.mysqlBackup.setUsername("root");
	}

	@Test
	public void testBackupLocal() throws IOException, InterruptedException {
		MySQLBackupTest.backupFile = MySQLBackupTest.mysqlBackup.backupDatabase("ibdbv1_groundnut_local");
	}

	@Test
	public void testRestore() throws Exception {
		MySQLBackupTest.mysqlBackup.restoreDatabase("ibdbv1_groundnut_local", MySQLBackupTest.backupFile, null);
	}
}
