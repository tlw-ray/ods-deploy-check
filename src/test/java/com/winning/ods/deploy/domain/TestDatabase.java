package com.winning.ods.deploy.domain;

import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.util.SqlServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class TestDatabase {
    @Before
    public void init() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    //TODO test generate JDBC connection string

    @Test
    public void testGetOdsRepository() throws SQLException, ClassNotFoundException {
        EtlRepository etlRepository = new EtlRepository();
        Database database = etlRepository.getBizDatabase();
        boolean result = SqlServer.testConnect(database);
        System.out.println(database.getJdbcConnectionString());
        System.out.println(database.getADOConnectionString());
        System.out.println(database.getOLEDBConnectionString());
        Assert.assertTrue(result);
    }

}
