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
import java.io.FileInputStream;
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

import org.springframework.beans.factory.annotation.Configurable;

/**
 * A class that provides methods for backing up and restoring MySQL databases.
 * 
 * @author Glenn Marintes
 */
@Configurable
public class MySQLUtil {
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
        
        String command = null;
        
        String mysqlDumpAbsolutePath = new File(this.mysqlDumpPath).getAbsolutePath();
        
        if (StringUtil.isEmpty(password)) {
            command = String.format("%s -n -c -h %s -P %d -u %s %s -r %s"
                    , mysqlDumpAbsolutePath, mysqlHost, mysqlPort, username, database, backupFilename);
        }
        else {
            command = String.format("%s -n -c -h %s -P %d -u %s -p%s %s -r %s"
                    , mysqlDumpAbsolutePath, mysqlHost, mysqlPort, username, password, database, backupFilename);
        }
        
        Process process = Runtime.getRuntime().exec(command);
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
    
    public void restoreDatabase(String databaseName, File backupFile) 
            throws IOException, SQLException {
        connect();
        
        try {
            restoreDatabase(connection, databaseName, backupFile);
        }
        finally {
            disconnect();
        }
    }
    
    public void restoreDatabase(Connection connection, String databaseName, File backupFile) 
            throws IOException, SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("connection parameter must not be null");
        }
        if (databaseName == null) {
            throw new IllegalArgumentException("databaseName parameter must not be null");
        }
        if (backupFile == null) {
            throw new IllegalArgumentException("backupFile parameter must not be null");
        }
        
        // backup the current database to a file
        File currentDbBackupFile = null;
        try {
            currentDbBackupFile = backupDatabase(databaseName, getBackupFilename(
                    databaseName, "system.sql"));
        }
        catch (InterruptedException e1) {
            throw new IllegalStateException("Unable to backup current database.");
        }
        
        // create the target database
        try {
            executeQuery(connection, "CREATE DATABASE IF NOT EXISTS " + databaseName);
            executeQuery(connection, "USE " + databaseName);
        }
        catch (SQLException e) {
            // ignore database creation error
        }
        
        // restore the backup
        try {
            restoreDatabaseWithFile(connection, backupFile);
        }
        catch (IOException e) {
            try {
                restoreDatabaseWithFile(connection, currentDbBackupFile);
            }
            catch (IOException e1) {
                String sorryMessage = "For some reason, the backup file cannot be restored"
                                    + " and your original database is now broken. I'm so sorry."
                                    + " If you have a backup file of your original database,"
                                    + " you can try to restore it.";
                throw new IllegalStateException(sorryMessage);
            }
        }
        
        
    }
    
    protected void restoreDatabaseWithFile(Connection connection, File backupFile) 
            throws IOException {
        if (backupFile == null) {
            return;
        }
        
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(backupFile)));
            scriptRunner.runScript(br);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    // intentionally empty
                }
            }
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
        
        // upgrade the database
        try {
            String disableFk = "SET FOREIGN_KEY_CHECKS=0";
            if (!executeUpdate(connection, disableFk)) {
                return false;
            }
            
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
                
                runScriptsInDirectory(connection, schemaUpdateDir, false, false);
            }
            
            return true;
        }
        finally {
            String enableFk = "SET FOREIGN_KEY_CHECKS=1";
            if (!executeUpdate(connection, enableFk)) {
                return false;
            }
        }
    }
    
    protected String getBackupFilename(String databaseName, String suffix) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd_hhmmss_SSS");
        String timestamp = format.format(new Date());
        
        String name = StringUtil.joinIgnoreEmpty("_", databaseName, timestamp, suffix);
        return StringUtil.joinIgnoreEmpty(File.separator, backupDir, name);
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
    
    public void runScriptsInDirectory(File directory) 
        throws IOException, SQLException {
        connect();

        try {
            runScriptsInDirectory(connection, directory);
        }
        finally {
            disconnect();
        }
    }
    
    public boolean runScriptsInDirectory(Connection conn, File directory) {
        return runScriptsInDirectory(conn, directory, true);
    }
    
    public boolean runScriptsInDirectory(Connection conn
            , File directory, boolean stopOnError) {
        return runScriptsInDirectory(conn, directory, stopOnError, true);
    }
    
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
    }
}
