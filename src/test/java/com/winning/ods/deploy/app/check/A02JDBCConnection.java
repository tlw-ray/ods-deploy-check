package com.winning.ods.deploy.app.check;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by tlw@winning.com.cn on 2017/6/14.
 */
public class A02JDBCConnection {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        Connection connection = DriverManager.getConnection("jdbc:sqlserver://10.64.5.48:1433;databaseName=CM_DataCenter;", "sa", "king-star123");
        Connection connection = DriverManager.getConnection("jdbc:sqlserver://10.64.5.49\\LIS;databaseName=LIS50;user=sa;password=P@ssw0rd");
//        Connection connection = DriverManager.getConnection("jdbc:sqlserver://10.64.5.49\\LIS:1433;databaseName=LIS50;user=sa;password=P@ssw0rd");

        connection.createStatement().executeQuery("select 1");
        connection.close();
    }
}
