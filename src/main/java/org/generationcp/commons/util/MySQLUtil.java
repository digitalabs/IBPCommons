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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * A class that provides methods for backing up and restoring MySQL databases.
 *
 * @author Glenn Marintes
 */
@Configurable
public class MySQLUtil {

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

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

	public void setMysqlPath(final String mysqlPath) {
		this.mysqlPath = mysqlPath;
	}

	public String getMysqlDumpPath() {
		return this.mysqlDumpPath;
	}

	public void setMysqlDumpPath(final String mysqlDumpPath) {
		this.mysqlDumpPath = mysqlDumpPath;
	}

	public String getBackupDir() {
		return this.backupDir;
	}

	public void setBackupDir(final String backupDir) {
		this.backupDir = backupDir;
	}

	public String getMysqlDriver() {
		return this.mysqlDriver;
	}

	public void setMysqlDriver(final String mysqlDriver) {
		this.mysqlDriver = mysqlDriver;
	}

	public String getMysqlHost() {
		return this.mysqlHost;
	}

	public void setMysqlHost(final String mysqlHost) {
		this.mysqlHost = mysqlHost;
	}

	public int getMysqlPort() {
		return this.mysqlPort;
	}

	public void setMysqlPort(final int mysqlPort) {
		this.mysqlPort = mysqlPort;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(final Connection connection) {
		this.connection = connection;
	}

	public ContextUtil getContextUtil() {
		return this.contextUtil;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void connect() throws SQLException {
		// load the JDBC driver
		if (this.mysqlDriver != null) {
			try {
				Class.forName(this.mysqlDriver);
			} catch (final ClassNotFoundException e) {
				throw new SQLException("Cannot connect to database", e);
			}
		}

		// connect
		if (this.mysqlHost != null) {
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.mysqlHost + ":" + this.mysqlPort + "/", this.username,
					this.password);
		}
	}

	public void disconnect() {
		try {
			this.connection.close();
		} catch (final SQLException e) {
			// intentionally empty
			MySQLUtil.LOG.debug("Error closing connection : " + e.getMessage());
		}
	}

	/**
	 * Exports the crop database using mysqldump into a single file
	 *
	 * The file is then concatenated with workbench data denoting the name of the program and most importantly the unique program id
	 *
	 */
	public File backupDatabase(final String database, final String backupFilename, final boolean includeProcedures)
			throws IOException, InterruptedException {
		final List<String> command = buildCommandStringList(database, backupFilename, includeProcedures);
		final ProcessBuilder pb = new ProcessBuilder(command);
		
		final Process process = pb.start();
		this.readProcessInputAndErrorStream(process);
		process.waitFor();

		final File file = new File(backupFilename);

		// append program information to the backup file
		// e.g. (2,9999,'MaizeProgramName','2015-12-06','78160def-b016-4071-b1c8-336f5c8b77b6','tutorial','2016-01-01 23:26:53');
		// the '9999' and 'tutorial' keyword are placeholders for the restoration
		if (file.exists()) {
			final String comment =
					"-- This backup file is for crop type " + this.contextUtil.getProjectInContext().getCropType().getCropName() + "\n";
			Files.write(Paths.get(backupFilename), comment.getBytes(), StandardOpenOption.APPEND);
			Files.write(Paths.get(backupFilename), "USE workbench;\n".getBytes(), StandardOpenOption.APPEND);
			
			// 'tutorial' values will be replaced with the proper ones from the crop in context upon restore
			final String workbenchCropValues = "INSERT into `workbench_crop` (`crop_name`, `db_name`, `schema_version`, `plot_code_prefix`) values ('tutorial','tutorial','4.0.0', '" + this.contextUtil.getProjectInContext().getCropType().getPlotCodePrefix() +"');\n";
			Files.write(Paths.get(backupFilename), workbenchCropValues.getBytes(),
					StandardOpenOption.APPEND);
			
			for (final Project program : this.workbenchDataManager
					.getProjectsByCrop(this.contextUtil.getProjectInContext().getCropType())) {
				final StringBuilder sb = buildProjectQueryString(program);
				Files.write(Paths.get(backupFilename), sb.toString().getBytes(), StandardOpenOption.APPEND);
			}
		}

		return file.exists() ? file.getAbsoluteFile() : null;
	}

	StringBuilder buildProjectQueryString(final Project program) {
		final StringBuilder sb = new StringBuilder();
		// sorry magic number here, will be replaced on restoration
		sb.append("INSERT into `workbench_project` (`project_id`, `user_id`, `project_name`, `start_date`, `project_uuid`, `crop_type`, `last_open_date`) values (null, 9999, '");
		sb.append(program.getProjectName());
		sb.append("','");
		sb.append(program.getStartDate());
		sb.append("','");
		sb.append(program.getUniqueID());
		// 'tutorial' crop will be replaced with the crop in context upon restore
		sb.append("','tutorial','");
		sb.append(program.getLastOpenDate());
		sb.append("');\n");
		MySQLUtil.LOG.info("Writing to Backup project Information : " + sb.toString());
		return sb;
	}

	List<String> buildCommandStringList(final String database, final String backupFilename,
			final boolean includeProcedures) {
		if (database == null || backupFilename == null) {
			return null;
		}

		final String mysqlDumpAbsolutePath = new File(this.mysqlDumpPath).getAbsolutePath();

		final List<String> command = new ArrayList<String>(Arrays.asList(mysqlDumpAbsolutePath, "--complete-insert", "--extended-insert",
				"--no-create-db", "--single-transaction", "--default-character-set=utf8", "--host=" + this.mysqlHost,
				"--port=" + this.mysqlPort, "--user=" + this.username, database, "-r", backupFilename));

		if (includeProcedures) {
			command.add(1, "--routines");
		}

		if (!StringUtil.isEmpty(this.password)) {
			command.add(1, "--password=" + this.password);
		}
		return command;
	}

	public void restoreDatabase(final String databaseName, final File backupFile, final Callable<Boolean> preRestoreTasks)
			throws Exception {
		this.connect();

		try {
			this.restoreDatabase(this.connection, databaseName, backupFile, preRestoreTasks);
		} finally {
			this.disconnect();
		}
	}

	public void restoreDatabase(final Connection connection, final String databaseName, final File backupFile,
			final Callable<Boolean> preRestoreTasks) throws Exception {
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

		// remove program records for dropped crop DB
		this.executeQuery(connection, "USE workbench");
		final List<String> programIdsToDelete =
				this.executeForManyStringResults(connection, "SELECT project_id from workbench_project where crop_type = '"
						+ this.contextUtil.getProjectInContext().getCropType().getCropName() + "';");
		for (final String programIdToDelete : programIdsToDelete) {
			this.executeQuery(connection, "DELETE FROM workbench.workbench_project_activity where project_id = " + programIdToDelete);
			this.executeQuery(connection, "DELETE FROM workbench.workbench_project_user_role where project_id = " + programIdToDelete);
			this.executeQuery(connection, "DELETE FROM workbench.workbench_ibdb_user_map where project_id = " + programIdToDelete);
			this.executeQuery(connection, "DELETE FROM workbench.workbench_project_user_info where project_id = " + programIdToDelete);
			this.executeQuery(connection, "DELETE FROM workbench.workbench_project where project_id = " + programIdToDelete);
		}

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
			this.addCurrentUserToRestoredPrograms(connection);

			// delete tutorial crop
			this.executeQuery(connection, "USE workbench;");
			this.executeQuery(connection, "DELETE from `workbench_crop` where db_name='tutorial';");

		} catch (final Exception e) {
			// fail restore using the selected backup, reverting to previous DB..
			MySQLUtil.LOG.error("Error encountered on restore " + e.getCause().getMessage(), e.getCause().getMessage());

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

				} catch (final Exception e2) {
					throw this.doRestoreToPreviousBackup(connection, databaseName, this.currentDbBackupFile, e);
				}
			} else {
				throw this.doRestoreToPreviousBackup(connection, databaseName, this.currentDbBackupFile, e);
			}
		}
	}

	private IllegalStateException doRestoreToPreviousBackup(final Connection connection, final String databaseName,
			final File currentDbBackupFile, final Exception e) {
		if (currentDbBackupFile != null) {
			try {

				MySQLUtil.LOG.debug("Trying to revert to the current state by restoring " + currentDbBackupFile.getAbsolutePath());

				this.executeQuery(connection, "DROP DATABASE IF EXISTS  " + databaseName);
				this.executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
				this.executeQuery(connection, "USE " + databaseName);

				this.runScriptFromFile(databaseName, currentDbBackupFile);
			} catch (final Exception e1) {
				// TODO review that message, change text to more appropriate and localise
				final String sorryMessage =
						"For some reason, the backup file cannot be restored" + " and your original database is now broken. I'm so sorry."
								+ " If you have a backup file of your original database," + " you can try to restore it.";
				return new IllegalStateException(sorryMessage, e1);
			}
		}
		return new IllegalStateException("Looks like there are errors in your SQL file. Please use another backup file.", e);
	}

	protected void backupUserPersonsBeforeRestoreDB(final Connection connection, final String databaseName) {
		try {
			this.executeQuery(connection, "CREATE DATABASE IF NOT EXISTS temp_db");
			this.executeQuery(connection, "USE temp_db");
			this.executeQuery(connection, "DROP table IF EXISTS users");
			this.executeQuery(connection, "CREATE TABLE users LIKE " + databaseName + ".users");
			this.executeQuery(connection, "INSERT users SELECT * FROM " + databaseName + ".users");
			this.executeQuery(connection, "DROP table IF EXISTS persons");
			this.executeQuery(connection, "CREATE TABLE persons LIKE " + databaseName + ".persons");
			this.executeQuery(connection, "INSERT persons SELECT * FROM " + databaseName + ".persons");
		} catch (final SQLException e) {
			MySQLUtil.LOG.error("Cannot backup users and persons table", e);
		}
	}

	protected void restoreUsersPersonsAfterRestoreDB(final Connection connection, final String databaseName) {
		try {
			this.executeQuery(connection, "USE " + databaseName);
			this.executeQuery(connection, "DROP table IF EXISTS users");
			this.executeQuery(connection, "CREATE TABLE users LIKE temp_db.users");
			this.executeQuery(connection, "INSERT users SELECT * FROM temp_db.users");
			this.executeQuery(connection, "DROP table IF EXISTS persons;");
			this.executeQuery(connection, "CREATE TABLE persons LIKE temp_db.persons");
			this.executeQuery(connection, "INSERT persons SELECT * FROM temp_db.persons");
		} catch (final SQLException e) {
			MySQLUtil.LOG.error("Cannot restore users and persons table", e);
		} finally {
			// make sure to drop temp_db regardless of errors
			try {
				this.executeQuery(connection, "DROP DATABASE IF EXISTS temp_db");
			} catch (final SQLException e) {
				MySQLUtil.LOG.error("Cannot drop temp_db", e);
			}
		}
	}

	protected void addCurrentUserToRestoredPrograms(final Connection connection) {
		final int currentUserId = this.contextUtil.getCurrentWorkbenchUserId();
		try {
			this.executeQuery(connection, "USE workbench");
			final List<String> programIds =
					this.executeForManyStringResults(connection, "SELECT project_id from workbench_project where user_id = '9999';");
			for (final String programKey : programIds) {
				this.executeQuery(connection,
						"INSERT into workbench_project_user_role values (null," + programKey + "," + currentUserId + ",1)");
				this.executeQuery(connection,
						"INSERT into workbench_project_user_info values (null," + programKey + "," + currentUserId + ",NOW())");
				this.executeQuery(connection,
						"INSERT into workbench_ibdb_user_map values (null," + currentUserId + "," + programKey + ",1)");
				this.executeQuery(connection, "UPDATE workbench_project set crop_type = '"
						+ this.contextUtil.getProjectInContext().getCropType().getCropName() + "' where project_id = " + programKey + ";");
			}
			this.executeQuery(connection, "UPDATE workbench_project set user_id = '" + currentUserId + "' where user_id = 9999;");
		} catch (final SQLException e) {
			MySQLUtil.LOG.error("Could not add current user to restored programs", e);
		}
	}

	protected void alterListNmsTable(final Connection connection, final String databaseName) {
		try {
			this.executeQuery(connection, "USE " + databaseName);
			this.executeQuery(connection, "ALTER TABLE " + databaseName + ".listnms DROP COLUMN notes");
		} catch (final SQLException e) {
			MySQLUtil.LOG.error("SQLException caught", e);
		}
	}

	public void runScriptFromFile(final String dbName, final File sqlFile) throws SQLFileException {
		ProcessBuilder pb;
		String mysqlAbsolutePath = new File("infrastructure/mysql/bin/mysql.exe").getAbsolutePath();
		if (this.mysqlPath != null) {
			mysqlAbsolutePath = new File(this.mysqlPath).getAbsolutePath();
		}
		MySQLUtil.LOG.debug("mysqlAbsolutePath = " + mysqlAbsolutePath);

		if (StringUtil.isEmpty(this.password)) {
			pb = new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort, "--user=" + this.username,
					"--default-character-set=utf8", dbName, "--execute=source " + sqlFile.getAbsoluteFile());

		} else {
			pb = new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort, "--user=" + this.username,
					"--password=" + this.password, "--default-character-set=utf8", dbName, "--execute=source " + sqlFile.getAbsoluteFile());
		}

		final Process mysqlRestoreProcess;
		try {
			mysqlRestoreProcess = pb.start();
			final String errorOut = this.readProcessInputAndErrorStream(mysqlRestoreProcess);

			final int exitValue = mysqlRestoreProcess.waitFor();
			MySQLUtil.LOG.debug("Process terminated with value " + exitValue);

			if (exitValue != 0) {
				MySQLUtil.LOG.error(errorOut);
				throw new IOException(errorOut);
			}
		} catch (IOException | InterruptedException e) {
			throw new SQLFileException(e);
		}

	}

	public void runScriptFromFile(final File sqlFile) throws SQLFileException {
		ProcessBuilder pb;
		String mysqlAbsolutePath = new File("infrastructure/mysql/bin/mysql.exe").getAbsolutePath();
		if (this.mysqlPath != null) {
			mysqlAbsolutePath = new File(this.mysqlPath).getAbsolutePath();
		}
		MySQLUtil.LOG.debug("mysqlAbsolutePath = " + mysqlAbsolutePath);

		if (StringUtil.isEmpty(this.password)) {
			pb = new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort, "--user=" + this.username,
					"--default-character-set=utf8", "--execute=source " + sqlFile.getAbsoluteFile());
		} else {
			pb = new ProcessBuilder(mysqlAbsolutePath, "--host=" + this.mysqlHost, "--port=" + this.mysqlPort, "--user=" + this.username,
					"--password=" + this.password, "--default-character-set=utf8", "--execute=source " + sqlFile.getAbsoluteFile());
		}

		final Process mysqlRestoreProcess;
		try {
			mysqlRestoreProcess = pb.start();
			this.readProcessInputAndErrorStream(mysqlRestoreProcess);

			final int exitValue = mysqlRestoreProcess.waitFor();
			MySQLUtil.LOG.debug("Process terminated with value " + exitValue);
			if (exitValue != 0) {
				// fail
				throw new IOException("Could not run the file:" + sqlFile.getAbsolutePath());
			}
		} catch (IOException | InterruptedException e) {
			throw new SQLFileException(e);
		}
	}

	private String readProcessInputAndErrorStream(final Process process) throws IOException {
		/*
		 * Added while loop to get input stream because process.waitFor() has a problem Reference:
		 * http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
		 */
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {

		}
		reader.close();
		/*
		 * When the process writes to stderr the output goes to a fixed-size buffer. If the buffer fills up then the process blocks until
		 * the buffer gets emptied. So if the buffer doesn't empty then the process will hang.
		 * http://stackoverflow.com/questions/10981969/why-is-going-through-geterrorstream-necessary-to-run-a-process
		 */
		final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		final StringBuilder errorOut = new StringBuilder();
		while ((line = errorReader.readLine()) != null) {
			errorOut.append(line);
		}

		errorReader.close();

		return errorOut.toString();
	}

	public String getBackupFilename(final String databaseName, final String suffix, final String customDir) {
		final DateFormat format = DateUtil.getSimpleDateFormat("yyyyMMdd_hhmmss_SSS");
		final String timestamp = format.format(new Date());

		final String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);

		final File bacKupCustomDir = new File(new File(customDir).getAbsolutePath());
		if (!bacKupCustomDir.exists() || !bacKupCustomDir.isDirectory()) {
			bacKupCustomDir.mkdirs();
		}

		return StringUtil.joinIgnoreEmpty(File.separator, bacKupCustomDir.getAbsolutePath(), name);
	}

	public void executeQuery(final Connection connection, final String query) throws SQLException {
		final Statement stmt = connection.createStatement();
		try {
			stmt.execute(query);

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public boolean executeUpdate(final Connection connection, final String query) throws SQLException {
		final Statement stmt = connection.createStatement();

		try {
			stmt.executeUpdate(query);
			return true;
		} catch (final SQLException e) {
			MySQLUtil.LOG.debug("Error executing query: " + query.toString());
			return false;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public String executeForStringResult(final Connection connection, final String query) throws SQLException {
		final Statement stmt = connection.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);

			if (rs.next()) {
				return rs.getString(1);
			}
			return null;
		} catch (final SQLException e) {
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

	/*
	 * Perhaps unnecessary redundancy here in duplication of executeForStringResult, but implemented this way to favour stability for now
	 *
	 */
	public List<String> executeForManyStringResults(final Connection connection, final String query) throws SQLException {
		final List<String> results = new ArrayList<>();
		final Statement stmt = connection.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				results.add(rs.getString(1));
			}

			return results;
		} catch (final SQLException e) {
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

	public boolean runScriptsInDirectory(final String databaseName, final File directory) throws SQLFileException {
		return this.runScriptsInDirectory(databaseName, directory, true);
	}

	public boolean runScriptsInDirectory(final String databaseName, final File directory, final boolean stopOnError)
			throws SQLFileException {
		return this.runScriptsInDirectory(databaseName, directory, stopOnError, true);
	}

	public boolean runScriptsInDirectory(final String databaseName, final File directory, final boolean stopOnError,
			final boolean logSqlError) throws SQLFileException {
		// get the sql files
		File[] sqlFilesArray = directory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".sql");
			}
		});
		if (sqlFilesArray == null) {
			sqlFilesArray = new File[0];
		}
		final List<File> sqlFiles = Arrays.asList(sqlFilesArray);
		Collections.sort(sqlFiles);

		for (final File sqlFile : sqlFiles) {
			MySQLUtil.LOG.info("Running script: " + sqlFile.getAbsolutePath());
			if (null != databaseName) {
				this.runScriptFromFile(databaseName, sqlFile);
			} else {
				this.runScriptFromFile(sqlFile);
			}
		}

		return true;
	}

	public boolean runScriptsInDirectory(final File directory) throws SQLFileException {
		return this.runScriptsInDirectory(null, directory, true);
	}

	public void updateOwnerships(final String databaseName, final Integer userId) throws IOException, SQLException {
		this.connect();

		try {
			this.executeQuery(this.connection, "USE " + databaseName);
			this.executeQuery(this.connection, "UPDATE LISTNMS SET LISTUID = " + userId);
		} finally {
			this.disconnect();
		}
	}

	public void dropSchemaVersion(final Connection connection, final String databaseName) throws IOException, SQLException {
		this.executeQuery(connection, "USE " + databaseName);
		this.executeQuery(connection, "DROP TABLE IF EXISTS schema_version");
	}

	public File createCurrentDbBackupFile(final String databaseName) throws IOException, InterruptedException {
		return this.backupDatabase(databaseName, this.getBackupFilename(databaseName, "system.sql", "temp"), true);
	}

	public void restoreDatabaseIfNotExists(final String databaseName, final String installationDirectory) {
		try {
			this.connect();
			try {
				this.executeQuery(this.connection, "USE " + databaseName);
			} catch (final Exception e) {
				final File backupFile = this.getLatestSystemBackupFile(installationDirectory, databaseName);
				try {
					this.executeQuery(this.connection, "DROP DATABASE IF EXISTS  " + databaseName);
					this.executeQuery(this.connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
					this.executeQuery(this.connection, "USE " + databaseName);
					this.runScriptFromFile(databaseName, backupFile);
				} catch (final Exception e1) {
					MySQLUtil.LOG.error(e.getMessage(), e1);
				}
			}
		} catch (final Exception e) {
			MySQLUtil.LOG.error(e.getMessage(), e);
		} finally {
			this.disconnect();
		}
	}

	private File getLatestSystemBackupFile(final String installationDirectory, final String databaseName) {
		final String backupFilenamePattern = databaseName + "_\\d+_\\d+_\\d+_system(.*).sql";
		final Pattern pattern = Pattern.compile(backupFilenamePattern);

		final File tempDirectory = new File(installationDirectory + "/" + "temp");
		final File[] filesInDir = tempDirectory.listFiles();
		String restoreFilename = null;
		for (final File file : filesInDir) {
			final String filename = file.getName();
			final Matcher matcher = pattern.matcher(filename);
			if (matcher.matches()) {
				if (restoreFilename != null) {
					final StringTokenizer currentFilenameTokens = new StringTokenizer(filename.substring(databaseName.length()), "_");
					final StringTokenizer previousFilenameTokens =
							new StringTokenizer(restoreFilename.substring(databaseName.length()), "_");
					while (currentFilenameTokens.hasMoreTokens()) {
						final String currentToken = currentFilenameTokens.nextToken();
						if (previousFilenameTokens.hasMoreTokens() && !currentToken.contains("system.sql")) {
							final String previousToken = previousFilenameTokens.nextToken();
							if (Integer.parseInt(currentToken) > Integer.parseInt(previousToken)) {
								restoreFilename = filename;
								break;
							}
						}
					}
				} else {
					restoreFilename = filename;
				}
			}
		}
		return new File(tempDirectory + "/" + restoreFilename);
	}
}
