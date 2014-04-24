/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

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
    
    public String getMysqlPath() {
        return mysqlPath;
    }

    public void setMysqlPath(String mysqlPath) {
        this.mysqlPath = mysqlPath;
    }

    public String getMysqlDumpPath() {
        return mysqlDumpPath;
    }

    public void setMysqlDumpPath(String mysqlDumpPath) {
        this.mysqlDumpPath = mysqlDumpPath;
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public String getMysqlDriver() {
        return mysqlDriver;
    }

    public void setMysqlDriver(String mysqlDriver) {
        this.mysqlDriver = mysqlDriver;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public void setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected void connect() throws SQLException {
        // load the JDBC driver
        if (mysqlDriver != null) {
            try {
                Class.forName(mysqlDriver);
            }
            catch (ClassNotFoundException e) {
                throw new SQLException("Cannot connect to database", e);
            }
        }
        
        // connect
        if (mysqlHost != null) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/", username, password);
        }
    }
    
    protected void disconnect() {
        try {
            connection.close();
        }
        catch (SQLException e) {
            // intentionally empty
        }
    }
    
    public File backupDatabase(String database) throws IOException, InterruptedException {
        if (database == null) {
            return null;
        }
        
        String backupFilename = getBackupFilename(database, ".sql");
        return backupDatabase(database, backupFilename);
    }
    
    public File backupDatabase(String database, String backupFilename) 
            throws IOException, InterruptedException {
        if (database == null || backupFilename == null) {
            return null;
        }
        
        String mysqlDumpAbsolutePath = new File(this.mysqlDumpPath).getAbsolutePath();
        
        ProcessBuilder pb = null;
        if (StringUtil.isEmpty(password)) {
            pb = new ProcessBuilder(mysqlDumpAbsolutePath
                                   ,"--complete-insert"
                                   ,"--extended-insert"
                                   ,"--no-create-db"
                                   ,"--single-transaction"
                                   ,"--default-character-set=utf8"
                                   ,"--host=" + mysqlHost
                                   ,"--port=" + mysqlPort
                                   ,"--user=" + username
                                   ,database
                                   ,"-r", backupFilename
                );
        }
        else {
            pb = new ProcessBuilder(mysqlDumpAbsolutePath
                                    ,"--complete-insert"
                                    ,"--extended-insert"
                                    ,"--no-create-db"
                                    ,"--single-transaction"
                                    ,"--default-character-set=utf8"
                                    ,"--host=" + mysqlHost
                                    ,"--port=" + mysqlPort
                                    ,"--user=" + username
                                    ,"--password=" + password
                                    ,database
                                    ,"-r", backupFilename
                 );
        }
        
        Process process = pb.start();
        readProcessInputAndErrorStream(process);
        process.waitFor();
        
        File file = new File(backupFilename);
        return file.exists() ? file.getAbsoluteFile() : null;
    }

    public File[] backupDatabases(String... databases) throws IOException, InterruptedException {
        if (databases == null || databases.length == 0) {
            return new File[0];
        }
        
        File[] backupFiles = new File[databases.length];
        
        for (int i=0; i < databases.length; i++) {
            backupFiles[i] = backupDatabase(databases[i]);
        }
        
        return backupFiles;
    }
    
    public void restoreDatabase(String databaseName, File backupFile,Callable<Boolean> preRestoreTasks) throws Exception {
    	restoreDatabase(databaseName, backupFile, null,preRestoreTasks);
    }
    
    public void restoreDatabase(String databaseName, File backupFile, File currentDbBackupFile,Callable<Boolean> preRestoreTasks)
            throws Exception {
        connect();
        
        try {
            restoreDatabase(connection, databaseName, backupFile, currentDbBackupFile,preRestoreTasks);
        }
        finally {
            disconnect();
        }
    }
    
    public void restoreDatabase(Connection connection, String databaseName, File backupFile, File currentDbBackupFile,Callable<Boolean> preRestoreTasks)
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

        // create the target database

        // backup current users + table in a temporary schema
        backupUserPersonsBeforeRestoreDB(connection,databaseName);

        executeQuery(connection,"DROP DATABASE IF EXISTS " + databaseName);
        //executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
        if (preRestoreTasks != null) {
            if (!preRestoreTasks.call()) {
                throw new Exception("Failure to generate LocalDB");
            }
        }

        executeQuery(connection, "USE " + databaseName);

        // restore the backup
        try {
        	LOG.debug("Trying to restore the original file " + backupFile.getAbsolutePath());
            runScriptFromFile(databaseName, backupFile);
            
            // after restore, restore from backup schema the users + persons table
            restoreUsersPersonsAfterRestoreDB(connection, databaseName);
        }
        catch (Exception e) {
            // fail restore using the selected backup, reverting to previous DB..
        	LOG.debug("Error encountered on restore ",e);
        	if(currentDbBackupFile!=null) {
	            try {
	                LOG.debug("Trying to revert to the current state by restoring "+currentDbBackupFile.getAbsolutePath());
	
	                executeQuery(connection,"DROP DATABASE IF EXISTS  " + databaseName);
	                executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
	                executeQuery(connection, "USE " + databaseName);
	
	                runScriptFromFile(databaseName, currentDbBackupFile);
	            }
	            catch (Exception e1) {
	                String sorryMessage = "For some reason, the backup file cannot be restored"
	                                    + " and your original database is now broken. I'm so sorry."
	                                    + " If you have a backup file of your original database,"
	                                    + " you can try to restore it.";
	                throw new IllegalStateException(sorryMessage);
	            }
        	}
            throw new IllegalStateException("The backup file cannot be restored. Restore has been canceled.");
        }
    }
    
    protected  void backupUserPersonsBeforeRestoreDB(Connection connection, String databaseName) {
        try {
            executeQuery(connection,"CREATE DATABASE IF NOT EXISTS temp_db");
            executeQuery(connection,"USE temp_db");
            executeQuery(connection,"DROP table IF EXISTS users");
            executeQuery(connection,"CREATE TABLE users LIKE " + databaseName + ".users");
            executeQuery(connection,"INSERT users SELECT * FROM " + databaseName +".users");
            executeQuery(connection,"DROP table IF EXISTS persons");
            executeQuery(connection,"CREATE TABLE persons LIKE " + databaseName + ".persons");
            executeQuery(connection,"INSERT persons SELECT * FROM " + databaseName +".persons");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected  void restoreUsersPersonsAfterRestoreDB(Connection connection, String databaseName) {
        try {
            executeQuery(connection,"USE " + databaseName);
            executeQuery(connection,"DROP table IF EXISTS users");
            executeQuery(connection,"CREATE TABLE users LIKE temp_db.users");
            executeQuery(connection,"INSERT users SELECT * FROM temp_db.users");
            executeQuery(connection,"DROP table IF EXISTS persons;");
            executeQuery(connection,"CREATE TABLE persons LIKE temp_db.persons");
            executeQuery(connection,"INSERT persons SELECT * FROM temp_db.persons");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // make sure to drop temp_db regardless of errors
            try {
                executeQuery(connection,"DROP DATABASE IF EXISTS temp_db");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }



    protected void runScriptFromFile(String dbName, File sqlFile) throws IOException, InterruptedException {
        ProcessBuilder pb;
        String mysqlAbsolutePath = new File("infrastructure/mysql/bin/mysql.exe").getAbsolutePath();
        if (mysqlPath != null)
            mysqlAbsolutePath = new File(mysqlPath).getAbsolutePath();
        LOG.debug("mysqlAbsolutePath = " + mysqlAbsolutePath);
        
        if (StringUtil.isEmpty(password)) {
            pb = new ProcessBuilder(mysqlAbsolutePath
            		,"--host=" + mysqlHost
                    ,"--port=" + mysqlPort
                    ,"--user=" + username
                    ,"--default-character-set=utf8"
                    ,dbName
                    ,"--execute=source " + sqlFile.getAbsoluteFile()+""
            );
        }
        else {
            pb = new ProcessBuilder(mysqlAbsolutePath
            		,"--host=" + mysqlHost
                    ,"--port=" + mysqlPort
                    ,"--user=" + username
                    , "--password=" + password
                    ,"--default-character-set=utf8"
                    ,dbName
                    ,"--execute=source " + sqlFile.getAbsoluteFile()+""
            );
        }

        Process mysqlRestoreProcess = pb.start();
        readProcessInputAndErrorStream(mysqlRestoreProcess);
        
        
        int exitValue = mysqlRestoreProcess.waitFor();
        LOG.debug("Process terminated with value "+exitValue);
        if (exitValue != 0) {
            // fail
        	throw new IOException("Could not restore the backup");
        } else {
            // success
        }
    }


    private void readProcessInputAndErrorStream(Process process) throws IOException {
    	/* Added while loop to get input stream because process.waitFor() has a problem
         * Reference: 
         * http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((reader.readLine()) != null) {}
        
        /* When the process writes to stderr the output goes to a fixed-size buffer. 
         * If the buffer fills up then the process blocks until the buffer gets emptied. 
         * So if the buffer doesn't empty then the process will hang.
         * http://stackoverflow.com/questions/10981969/why-is-going-through-geterrorstream-necessary-to-run-a-process
         */
    	BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((errorReader.readLine()) != null) {
        	LOG.debug(errorReader.readLine());
        }
	}

	/**
     * Update the specified database using scripts from the specified <code>updateDir</code>.
     * 
     * @param databaseName
     * @param updateDir
     * @throws IOException
     * @throws SQLException
     */
    public boolean upgradeDatabase(String databaseName, File updateDir) 
        throws IOException, SQLException {
        connect();

        try {
            return upgradeDatabase(connection, databaseName, updateDir);
        }
        finally {
            disconnect();
        }
    }
    
    public boolean upgradeDatabase(Connection connection, String databaseName, File updateDir) 
        throws IOException, SQLException {
    	
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
        
        // use the target database
        try {
            executeQuery(connection, "USE " + databaseName);
        }
        catch (SQLException e) {
            // ignore database creation error
        }
        
     // check our schema version
        String currentSchemaVersion = null;
        try {
            // get the current version of the database
            currentSchemaVersion = executeForStringResult(connection, "SELECT version FROM schema_version ORDER BY version DESC LIMIT 1");
        }
        catch (SQLException e) {
            // assume old schema if there is an SQL error
        }
        LOG.debug("Executing upgradeDatabase from directory "+updateDir.getAbsolutePath());
        LOG.debug("Applying upgrade in database "+databaseName);
        LOG.debug("The schema version is "+currentSchemaVersion);
        
        // upgrade the database
        try {
            String disableFk = "SET FOREIGN_KEY_CHECKS=0";
            if (!executeUpdate(connection, disableFk)) {
                return false;
            }
            LOG.debug("Disabling foreign key checks...");
            
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
                LOG.debug("Running scripts from directory: "+schemaUpdateDir.getAbsolutePath());
                //runScriptsInDirectory(connection, schemaUpdateDir, false, false);
                runScriptsInDirectory(databaseName,schemaUpdateDir,false,false);

            }
            
            return true;
        }
        finally {
            String enableFk = "SET FOREIGN_KEY_CHECKS=1";
            if (!executeUpdate(connection, enableFk)) {
                return false;
            }
            LOG.debug("Enabling foreign key checks...");
        }
    }
    
    protected String getBackupFilename(String databaseName, String suffix) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss_SSS");
        String timestamp = format.format(new Date());

        String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);
        return StringUtil.joinIgnoreEmpty(File.separator, (new File(backupDir)).getAbsolutePath(), name);
    }

    protected String getBackupFilename(String databaseName, String suffix,String customDir) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss_SSS");
        String timestamp = format.format(new Date());

        String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);

        File _customDir = new File(new File(customDir).getAbsolutePath());
        if (!_customDir.exists() || !_customDir.isDirectory())
            _customDir.mkdirs();

        return StringUtil.joinIgnoreEmpty(File.separator, _customDir.getAbsolutePath(), name);
    }
    
    public void executeQuery(Connection connection, String query) throws SQLException {
        Statement stmt = connection.createStatement();
        
        try {
            stmt.execute(query);
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
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
        }
        catch (SQLException e) {
            return false;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    public String executeForStringResult(Connection connection, String query) 
            throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                finally {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                finally {
                }
            }
        }
    }
    

    public boolean runScriptsInDirectory(String databaseName, File directory) {
        return runScriptsInDirectory(databaseName, directory, true);
    }
    
    public boolean runScriptsInDirectory(String databaseName
            , File directory, boolean stopOnError) {
        return runScriptsInDirectory(databaseName, directory, stopOnError, true);
    }

    /*
    public boolean runScriptsInDirectory(Connection conn
            , File directory, boolean stopOnError, boolean logSqlError) {
    	// get the sql files
        File[] sqlFilesArray = directory.listFiles(new FilenameFilter() {
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
            BufferedReader br = null;
            
            try {
            	LOG.debug("Running script: "+sqlFile.getAbsolutePath());

                br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));
                
                ScriptRunner runner = new ScriptRunner(conn, false, stopOnError);
                runner.runScript(br);

            }
            catch (IOException e1) {
            }
            finally {
                if (br != null) {
                    try {
                        br.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return true;
    } */

    public boolean runScriptsInDirectory(String databaseName
            , File directory, boolean stopOnError, boolean logSqlError) {
        // get the sql files
        File[] sqlFilesArray = directory.listFiles(new FilenameFilter() {
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
            try {
                LOG.debug("Running script: "+sqlFile.getAbsolutePath());

                /*
                br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));

                ScriptRunner runner = new ScriptRunner(conn, false, stopOnError);
                runner.runScript(br);
                */

                runScriptFromFile(databaseName, sqlFile);
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void updateOwnerships(String databaseName, Integer userId) 
            throws IOException, SQLException {
        connect();
        
        try {
        	executeQuery(connection, "USE " + databaseName);
        	executeQuery(connection, "UPDATE LISTNMS SET LISTUID = "+userId);
        }
        finally {
            disconnect();
        }
    }
    
    public void dropSchemaVersion(String databaseName) 
            throws IOException, SQLException {
        connect();
        
        try {
        	executeQuery(connection, "USE " + databaseName);
        	executeQuery(connection, "DROP TABLE IF EXISTS schema_version");
        }
        finally {
            disconnect();
        }
    }
    
    public void restoreOriginalState(String databaseName, File currentDbBackupFile) throws Exception {
    	if(currentDbBackupFile!=null && currentDbBackupFile.exists()) {
    		connect();

            try {
            	LOG.debug("Trying to revert to the current state by restoring "+currentDbBackupFile.getAbsolutePath());
            	executeQuery(connection,"DROP DATABASE IF EXISTS  " + databaseName);
            	executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
            	executeQuery(connection, "USE " + databaseName);

            	runScriptFromFile(databaseName, currentDbBackupFile);
            }
            finally {
            	disconnect();
            }
        }
        
    }
    
    public File createCurrentDbBackupFile(String databaseName) throws Exception {
    	 connect();

         try {
        	 return backupDatabase(databaseName, getBackupFilename(
 	                databaseName, "system.sql","temp"));
         }
         finally {
             disconnect();
         }
    	
    }
}
