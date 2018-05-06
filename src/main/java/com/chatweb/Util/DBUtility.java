/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatweb.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TAND.M
 */
public class DBUtility {

    private static Connection connection = null;
    private static String hostName = "localhost";
    private static String dbName = "chat";
    private static String user = "root";
    private static String pass = "";

    public Connection getConnection() throws SQLException {
        if (connection != null && connection.isValid(0)) {
            return connection;
        } else {
            if (connection != null) {
                connection.close();
                connection = null;
            }
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Logger.getLogger(DBUtility.class.getName()).log(Level.SEVERE, null, e);
            }

            String url = "jdbc:mysql://" + hostName + ":3306/" + dbName;
            connection = DriverManager.getConnection(url, user, pass);
            return connection;
        }
    }

    public boolean checkConnectLogin(boolean val) {
        if (val) {
            connection = null;
            return true;
        } else {
            return false;
        }
    }
}
