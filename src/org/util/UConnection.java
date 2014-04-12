/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */

package org.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcos Avila Weingartshofer
 */
public class UConnection {
    
    private static final String MYSQL_PREFIX = "jdbc:mysql://";
    private static final String ORACLE_PREFIX = "jdbc:oracle:thin:@";
    
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
    
    private  Connection connection;
    private  Statement statement;
    // construye la conexión
    private String prefixDriver;
    private String dns;
    private String port;
    private String dbName;
    private String driver;
    
    private String dbType;
    // private static final String DNS = "jdbc:mysql://localhost/bd";
    
    // usuario y contraseña para la conexión
    private String user;
    private String pass;

    public UConnection(String dbType, String url, String port, String dbName, String user, String pass) {
        this.dns = url;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.pass = pass;
        
        this.dbType = dbType;
        
        _setDriver(dbType);
        
        _builConnection();
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() {
        try {
            if(connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error when closing connection!!!");
        }
    }
    
    private void _setDriver(String t) {
        if(t.equals("oracle")){
            driver = ORACLE_DRIVER;
            prefixDriver = ORACLE_PREFIX;
        }
        
        if(t.equals("mysql")) {
            driver = MYSQL_DRIVER;
            prefixDriver = MYSQL_PREFIX;
        }
    }


    private void _builConnection() {
        if(connection != null) {
            return;
        }
        
        String url = prefixDriver + dns;
        if(!port.equals("")) {
            url += ":" + port;
        }
        
        if(dbType.equals("oracle")){
            url += ":" + dbName;
        }
        
        if(dbType.equals("mysql")) {
            url += "/" + dbName;
        }
        
        try {
            System.out.println("url: " + url);
            Class.forName(driver); // can remove this statement
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection succesfully...");
        } catch(SQLException  e) {
            throw new RuntimeException("conexión no exitosa");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
