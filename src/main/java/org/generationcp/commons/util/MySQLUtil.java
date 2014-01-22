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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            command = String.format("%s -n -c -P %d -u %s %s -r %s"
                    , mysqlDumpAbsolutePath, mysqlPort, username, database, backupFilename);
        }
        else {
            command = String.format("%s -n -c -P %d -u %s -p%s %s -r %s"
                    , mysqlDumpAbsolutePath, mysqlPort, username, password, database, backupFilename);
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
        
        //add Notes column to listnms table if not exists
        try{
        	executeQuery(connection, "ALTER TABLE listnms ADD COLUMN notes TEXT NULL DEFAULT NULL");
        } catch(SQLException e){
        	
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
}
