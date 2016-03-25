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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.generationcp.commons.exceptions.SQLFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A class that provides methods for backing up and restoring MySQL databases.
 *
 * @author Glenn Marintes
 */
@Configurable
public class MySQLUtil {

	private static final Logger LOG = LoggerFactory.getLogger(MySQLUtil.class);

	private String mysqlPath;
	private String mysqlDumpPath;

	private String backupDir;

	private String mysqlDriver = "com.mysql.jdbc.Driver";
	private String mysqlHost;
	private int mysqlPort = 3306;
	private String username;
	private String password;

	private Connection connection;

	private File currentDbBackupFile;

	public String getMysqlPath() {
		return this.mysqlPath;
	}

	public void setMysqlPath(String mysqlPath) {
		this.mysqlPath = mysqlPath;
	}

	public String getMysqlDumpPath() {
		return this.mysqlDumpPath;
	}

	public void setMysqlDumpPath(String mysqlDumpPath) {
		this.mysqlDumpPath = mysqlDumpPath;
	}

	public String getBackupDir() {
		return this.backupDir;
	}

	public void setBackupDir(String backupDir) {
		this.backupDir = backupDir;
	}

	public String getMysqlDriver() {
		return this.mysqlDriver;
	}

	public void setMysqlDriver(String mysqlDriver) {
		this.mysqlDriver = mysqlDriver;
	}

	public String getMysqlHost() {
		return this.mysqlHost;
	}

	public void setMysqlHost(String mysqlHost) {
		this.mysqlHost = mysqlHost;
	}

	public int getMysqlPort() {
		return this.mysqlPort;
	}

	public void setMysqlPort(int mysqlPort) {
		this.mysqlPort = mysqlPort;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void connect() throws SQLException {
		// load the JDBC driver
		if (this.mysqlDriver != null) {
			try {
				Class.forName(this.mysqlDriver);
			} catch (ClassNotFoundException e) {
				throw new SQLException("Cannot connect to database", e);
			}
		}

		// connect
		if (this.mysqlHost != null) {
			this.connection =
					DriverManager
							.getConnection("jdbc:mysql://" + this.mysqlHost + ":" + this.mysqlPort + "/", this.username, this.password);
		}
	}

	public void disconnect() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			// intentionally empty
			MySQLUtil.LOG.debug("Error closing connection");
		}
	}

	public File backupDatabase(String database) throws IOException, InterruptedException {
		if (database == null) {
			return null;
		}

		String backupFilename = this.getBackupFilename(database, ".sql");
		return this.backupDatabase(database, backupFilename, false);
	}

	public File backupDatabase(String database, String backupFilename, boolean includeProcedures) throws IOException, InterruptedException {
		if (database == null || backupFilename == null) {
			return null;
		}

		String mysqlDumpAbsolutePath = new File(this.mysqlDumpPath).getAbsolutePath();

		List<String> command =
				new ArrayList<>(Arrays.asList(mysqlDumpAbsolutePath, "--complete-insert", "--extended-insert", "--no-create-db",
						"--single-transaction", "--default-character-set=utf8", "--host=" + this.mysqlHost, "--port=" + this.mysqlPort,
						"--user=" + this.username, database, "-r", backupFilename));

		if (includeProcedures) {
			command.add(1, "--routines");
		}

		if (!StringUtil.isEmpty(this.password)) {
			command.add(1, "--password=" + this.password);
		}

		ProcessBuilder pb = new ProcessBuilder(command);

		Process process = pb.start();
		this.readProcessInputAndErrorStream(process);
		process.waitFor();

		File file = new File(backupFilename);
		return file.exists() ? file.getAbsoluteFile() : null;
	}

	public File[] backupDatabases(String... databases) throws IOException, InterruptedException {
		if (databases == null || databases.length == 0) {
			return new File[0];
		}

		File[] backupFiles = new File[databases.length];

		for (int i = 0; i < databases.length; i++) {
			backupFiles[i] = this.backupDatabase(databases[i]);
		}

		return backupFiles;
	}

	public void restoreDatabase(String databaseName, File backupFile, Callable<Boolean> preRestoreTasks) throws Exception {
		this.connect();

		try {
			this.restoreDatabase(this.connection, databaseName, backupFile, preRestoreTasks);
		} finally {
			this.disconnect();
		}
	}

	public void restoreDatabase(Connection connection, String databaseName, File backupFile, Callable<Boolean> preRestoreTasks)
			throws Exception {
		if (connection == null) {
			throw new IllegalArgumentException("connection parameter must not be null");
		}
		if (databaseName == null) {
			throw new IllegalArgumentException("databaseName parameter must not be null");
		}
		if (backupFile == null) {
			throw new IllegalArgumentException("sqlFile parameter must not be null");
		}

		// backup current users + table in a temporary schema
		this.backupUserPersonsBeforeRestoreDB(connection, databaseName);

		// 05-08-14 Backup current DB + Stored Procedure
		this.currentDbBackupFile = this.createCurrentDbBackupFile(databaseName);

		this.executeQuery(connection, "DROP DATABASE IF EXISTS " + databaseName);

		// CREATE LOCAL DB INSTANCE
		if (preRestoreTasks != null && !preRestoreTasks.call()) {
			throw new Exception("Failure to generate LocalDB");
		}

		this.executeQuery(connection, "USE " + databaseName);

		// restore the backup
		try {
			// this is needed because the schema_version of the file must be followed
			this.dropSchemaVersion(connection, databaseName);

			MySQLUtil.LOG.debug("Trying to restore the original file " + backupFile.getAbsolutePath());
			this.runScriptFromFile(databaseName, backupFile);

			// after restore, restore from backup schema the users + persons table
			this.restoreUsersPersonsAfterRestoreDB(connection, databaseName);

		} catch (Exception e) {
			// fail restore using the selected backup, reverting to previous DB..
			MySQLUtil.LOG.error("Error encountered on restore", e);

			// GCP-7192 (Workaround) If insert data to listnms script fails and throws an error
			// "Column count doesn't match value count at row 1"
			// try to adjust the table schema so the script will be executed successfully.
			// ERROR 1136 = Column count doesn't match value count at row 1
			if (e.getMessage().contains("ERROR 1136 ")) {
				try {
					this.executeQuery(connection, "DROP DATABASE IF EXISTS " + databaseName);

					if (preRestoreTasks != null && !preRestoreTasks.call()) {
						throw new Exception("Failure to generate LocalDB");
					}

					this.executeQuery(connection, "USE " + databaseName);

					// delete the notes field
					this.alterListNmsTable(connection, databaseName);

					this.runScriptFromFile(databaseName, backupFile);

					// after restore, restore from backup schema the users + persons table
					this.restoreUsersPersonsAfterRestoreDB(connection, databaseName);

				} catch (Exception e2) {
					throw this.doRestoreToPreviousBackup(connection, databaseName, this.currentDbBackupFile, e);
				}
			} else {
				throw this.doRestoreToPreviousBackup(connection, databaseName, this.currentDbBackupFile, e);
			}
		}
	}

	private IllegalStateException doRestoreToPreviousBackup(Connection connection, String databaseName, File currentDbBackupFile,
			Exception e) {
		if (currentDbBackupFile != null) {
			try {

				MySQLUtil.LOG.debug("Trying to revert to the current state by restoring " + currentDbBackupFile.getAbsolutePath());

				this.executeQuery(connection, "DROP DATABASE IF EXISTS  " + databaseName);
				this.executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
				this.executeQuery(connection, "USE " + databaseName);

				this.runScriptFromFile(databaseName, currentDbBackupFile);
			} catch (Exception e1) {
				String sorryMessage =
						"For some reason, the backup file cannot be restored" + " and your original database is now broken. I'm so sorry."
								+ " If you have a backup file of your original database," + " you can try to restore it.";
				return new IllegalStateException(sorryMessage, e1);
			}
		}
		return new IllegalStateException("Looks like there are errors in your SQL file. Please use another backup file.", e);
	}

	protected void backupUserPersonsBeforeRestoreDB(Connection connection, String databaseName) {
		try {
			this.executeQuery(connection, "CREATE DATABASE IF NOT EXISTS temp_db");
			this.executeQuery(connection, "USE temp_db");
			this.executeQuery(connection, "DROP table IF EXISTS users");
			this.executeQuery(connection, "CREATE TABLE users LIKE " + databaseName + ".users");
			this.executeQuery(connection, "INSERT users SELECT * FROM " + databaseName + ".users");
			this.executeQuery(connection, "DROP table IF EXISTS persons");
			this.executeQuery(connection, "CREATE TABLE persons LIKE " + databaseName + ".persons");
			this.executeQuery(connection, "INSERT persons SELECT * FROM " + databaseName + ".persons");
		} catch (SQLException e) {
			MySQLUtil.LOG.error("Cannot backup users and persons table", e);
		}
	}

	protected void restoreUsersPersonsAfterRestoreDB(Connection connection, String databaseName) {
		try {
			this.executeQuery(connection, "USE " + databaseName);
			this.executeQuery(connection, "DROP table IF EXISTS users");
			this.executeQuery(connection, "CREATE TABLE users LIKE temp_db.users");
			this.executeQuery(connection, "INSERT users SELECT * FROM temp_db.users");
			this.executeQuery(connection, "DROP table IF EXISTS persons;");
			this.executeQuery(connection, "CREATE TABLE persons LIKE temp_db.persons");
			this.executeQuery(connection, "INSERT persons SELECT * FROM temp_db.persons");
		} catch (SQLException e) {
			MySQLUtil.LOG.error("Cannot restore users and persons table", e);
		} finally {
			// make sure to drop temp_db regardless of errors
			try {
				this.executeQuery(connection, "DROP DATABASE IF EXISTS temp_db");
			} catch (SQLException e) {
				MySQLUtil.LOG.error("Cannot drop temp_db", e);
			}
		}
	}

	protected void alterListNmsTable(Connection connection, String databaseName) {
		try {

			this.executeQuery(connection, "USE " + databaseName);
			this.executeQuery(connection, "ALTER TABLE " + databaseName + ".listnms DROP COLUMN notes");
		} catch (SQLException e) {
			MySQLUtil.LOG.error("SQLException caught", e);
		}
	}

	public void runScriptFromFile(String dbName, File sqlFile) throws SQLFileException {
		ProcessBuilder pb;
		String mysqlAbsolutePath = new File("infrastructure/mysql/bin/mysql.exe").getAbsolutePath();
		if (this.mysqlPath != null) {
			mysqlAbsolutePath = new File(this.mysqlPath).getAbsolutePath();
		}
		MySQLUtil.LOG.debug("mysqlAbsolutePath = " + mysqlAbsolutePath);

		if (StringUtil.isEmpty(this.password)) {
			pb =
					new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort,
							"--user=" + this.username, "--default-character-set=utf8", dbName, "--execute=source "
									+ sqlFile.getAbsoluteFile());

		} else {
			pb =
					new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort,
							"--user=" + this.username, "--password=" + this.password, "--default-character-set=utf8", dbName,
							"--execute=source " + sqlFile.getAbsoluteFile());
		}

		Process mysqlRestoreProcess;
		try {
			mysqlRestoreProcess = pb.start();
			String errorOut = this.readProcessInputAndErrorStream(mysqlRestoreProcess);

			int exitValue = mysqlRestoreProcess.waitFor();
			MySQLUtil.LOG.debug("Process terminated with value " + exitValue);

			if (exitValue != 0) {
				MySQLUtil.LOG.error(errorOut);
				throw new IOException(errorOut);
			}
		} catch (IOException | InterruptedException e) {
			throw new SQLFileException(e);
		}

	}

	public void runScriptFromFile(File sqlFile) throws SQLFileException {
		ProcessBuilder pb;
		String mysqlAbsolutePath = new File("infrastructure/mysql/bin/mysql.exe").getAbsolutePath();
		if (this.mysqlPath != null) {
			mysqlAbsolutePath = new File(this.mysqlPath).getAbsolutePath();
		}
		MySQLUtil.LOG.debug("mysqlAbsolutePath = " + mysqlAbsolutePath);

		if (StringUtil.isEmpty(this.password)) {
			pb =
					new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort,
							"--user=" + this.username, "--default-character-set=utf8", "--execute=source " + sqlFile.getAbsoluteFile());
		} else {
			pb =
					new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort,
							"--user=" + this.username, "--password=" + this.password, "--default-character-set=utf8", "--execute=source "
									+ sqlFile.getAbsoluteFile());
		}

		Process mysqlRestoreProcess;
		try {
			mysqlRestoreProcess = pb.start();
			this.readProcessInputAndErrorStream(mysqlRestoreProcess);

			int exitValue = mysqlRestoreProcess.waitFor();
			MySQLUtil.LOG.debug("Process terminated with value " + exitValue);
			if (exitValue != 0) {
				// fail
				throw new IOException("Could not run the file:" + sqlFile.getAbsolutePath());
			}
		} catch (IOException | InterruptedException e) {
			throw new SQLFileException(e);
		}
	}

	private String readProcessInputAndErrorStream(Process process) throws IOException {
		/*
		 * Added while loop to get input stream because process.waitFor() has a problem Reference:
		 * http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {

		}
		reader.close();
		/*
		 * When the process writes to stderr the output goes to a fixed-size buffer. If the buffer fills up then the process blocks until
		 * the buffer gets emptied. So if the buffer doesn't empty then the process will hang.
		 * http://stackoverflow.com/questions/10981969/why-is-going-through-geterrorstream-necessary-to-run-a-process
		 */
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		StringBuilder errorOut = new StringBuilder();
		while ((line = errorReader.readLine()) != null) {
			errorOut.append(line);
		}

		errorReader.close();

		return errorOut.toString();
	}

	/**
	 * Update the specified database using scripts from the specified <code>updateDir</code>.
	 *
	 * @param databaseName
	 * @param updateDir
	 * @throws IOException
	 * @throws SQLException
	 */
	public boolean upgradeDatabase(String databaseName, File updateDir) throws Exception {
		this.connect();

		try {
			return this.upgradeDatabase(this.connection, databaseName, updateDir);
		} finally {
			this.disconnect();
		}
	}

	public boolean upgradeDatabase(Connection connection, String databaseName, File updateDir) throws Exception {

		if (connection == null) {
			throw new IllegalArgumentException("connection parameter must not be null");
		}
		if (databaseName == null) {
			throw new IllegalArgumentException("databaseName parameter must not be null");
		}
		if (updateDir == null) {
			throw new IllegalArgumentException("updateDir parameter must not be null");
		}
		if (!updateDir.exists()) {
			return true;
		}

		if (this.currentDbBackupFile == null) {
			this.currentDbBackupFile = this.createCurrentDbBackupFile(databaseName);
		}

		// use the target database
		try {
			this.executeQuery(connection, "USE " + databaseName);
		} catch (SQLException e) {
			// ignore database creation error
			MySQLUtil.LOG.debug("Could not access current DB: " + databaseName);
		}

		// check our schema version
		String currentSchemaVersion = null;
		try {
			// get the current version of the database
			currentSchemaVersion =
					this.executeForStringResult(connection, "SELECT version FROM schema_version ORDER BY version DESC LIMIT 1");
		} catch (SQLException e) {
			// assume old schema if there is an SQL error
			MySQLUtil.LOG.debug("Could not query value for schema_version");
		}
		MySQLUtil.LOG.debug("Executing upgradeDatabase from directory " + updateDir.getAbsolutePath());
		MySQLUtil.LOG.debug("Applying upgrade in database " + databaseName);
		MySQLUtil.LOG.debug("The schema version is " + currentSchemaVersion);

		// upgrade the database
		try {
			String disableFk = "SET FOREIGN_KEY_CHECKS=0";
			if (!this.executeUpdate(connection, disableFk)) {
				return false;
			}
			MySQLUtil.LOG.debug("Disabling foreign key checks...");

			// get the list of schema versions included in the installer
			List<File> schemaUpdateList = Arrays.asList(updateDir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.exists() && pathname.isDirectory();
				}
			}));
			Collections.sort(schemaUpdateList);

			// execute schema updates for each version greater than the current version
			for (File schemaUpdateDir : schemaUpdateList) {
				if (currentSchemaVersion != null) {
					int compareResult = schemaUpdateDir.getName().compareTo(currentSchemaVersion);
					if (compareResult <= 0) {
						continue;
					}
				}
				MySQLUtil.LOG.debug("Running scripts from directory: " + schemaUpdateDir.getAbsolutePath());

				this.runScriptsInDirectory(databaseName, schemaUpdateDir, false, false);

			}
			return true;
		} catch (Exception e) {
			throw this.doRestoreToPreviousBackup(connection, databaseName, this.currentDbBackupFile, e);
		} finally {
			MySQLUtil.LOG.debug("Enabling foreign key checks...");
			String enableFk = "SET FOREIGN_KEY_CHECKS=1";
			this.executeUpdate(connection, enableFk);
		}
	}

	protected String getBackupFilename(String databaseName, String suffix) {
		DateFormat format = DateUtil.getSimpleDateFormat("yyyyMMdd_hhmmss_SSS");
		String timestamp = format.format(new Date());

		String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);
		return StringUtil.joinIgnoreEmpty(File.separator, new File(this.backupDir).getAbsolutePath(), name);
	}

	protected String getBackupFilename(String databaseName, String suffix, String customDir) {
		DateFormat format = DateUtil.getSimpleDateFormat("yyyyMMdd_hhmmss_SSS");
		String timestamp = format.format(new Date());

		String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);

		File bacKupCustomDir = new File(new File(customDir).getAbsolutePath());
		if (!bacKupCustomDir.exists() || !bacKupCustomDir.isDirectory()) {
			bacKupCustomDir.mkdirs();
		}

		return StringUtil.joinIgnoreEmpty(File.separator, bacKupCustomDir.getAbsolutePath(), name);
	}

	public void executeQuery(Connection connection, String query) throws SQLException {
		Statement stmt = connection.createStatement();

		try {
			stmt.execute(query);

		} catch (SQLException e) {
			throw e;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public boolean executeUpdate(Connection connection, String query) throws SQLException {
		Statement stmt = connection.createStatement();

		try {
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			MySQLUtil.LOG.debug("Error executing query: " + query.toString());
			return false;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public String executeForStringResult(Connection connection, String query) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);

			if (rs.next()) {
				return rs.getString(1);
			}
			return null;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public boolean runScriptsInDirectory(String databaseName, File directory) throws SQLFileException {
		return this.runScriptsInDirectory(databaseName, directory, true);
	}

	public boolean runScriptsInDirectory(String databaseName, File directory, boolean stopOnError) throws SQLFileException {
		return this.runScriptsInDirectory(databaseName, directory, stopOnError, true);
	}

	public boolean runScriptsInDirectory(String databaseName, File directory, boolean stopOnError, boolean logSqlError)
			throws SQLFileException {
		// get the sql files
		File[] sqlFilesArray = directory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sql");
			}
		});
		if (sqlFilesArray == null) {
			sqlFilesArray = new File[0];
		}
		List<File> sqlFiles = Arrays.asList(sqlFilesArray);
		Collections.sort(sqlFiles);

		for (File sqlFile : sqlFiles) {
			MySQLUtil.LOG.debug("Running script: " + sqlFile.getAbsolutePath());
			if (null != databaseName) {
				this.runScriptFromFile(databaseName, sqlFile);
			} else {
				this.runScriptFromFile(sqlFile);
			}
		}

		return true;
	}

	public boolean runScriptsInDirectory(File directory) throws SQLFileException {
		return this.runScriptsInDirectory(null, directory, true);
	}

	public void updateOwnerships(String databaseName, Integer userId) throws IOException, SQLException {
		this.connect();

		try {
			this.executeQuery(this.connection, "USE " + databaseName);
			this.executeQuery(this.connection, "UPDATE LISTNMS SET LISTUID = " + userId);
		} finally {
			this.disconnect();
		}
	}

	public void dropSchemaVersion(Connection connection, String databaseName) throws IOException, SQLException {
		this.executeQuery(connection, "USE " + databaseName);
		this.executeQuery(connection, "DROP TABLE IF EXISTS schema_version");
	}

	public File createCurrentDbBackupFile(String databaseName) throws IOException, InterruptedException {
		return this.backupDatabase(databaseName, this.getBackupFilename(databaseName, "system.sql", "temp"), true);
	}
}
