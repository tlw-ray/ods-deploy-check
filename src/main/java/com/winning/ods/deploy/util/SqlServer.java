package com.winning.ods.deploy.util;

import com.winning.ods.deploy.domain.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class SqlServer {

    static Logger logger = LoggerFactory.getLogger(SqlServer.class);

    //测试连接
    public static boolean testConnect(Database database) throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionStr = database.getJdbcConnectionString();
        logger.info(connectionStr);
        Connection connection = DriverManager.getConnection(connectionStr);
        connection.createStatement().executeQuery("select 1");
        connection.close();
        return true;
    }
}
