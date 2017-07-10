package com.winning.ods.deploy.dao;

import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class TestRepository {
    @Before
    public void init() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Test
    public void testGetFieldInfo() throws SQLException {
        EtlRepository etlRepository = new EtlRepository();

        Repository repository = etlRepository.fetchBizRepository().get(0);
        Set<Pair<String, String>> fieldTableSet = etlRepository.fetchDefineTableField(repository.getBizDatabase().getBizName());
        Set<String> tableNameSet = new HashSet();
        fieldTableSet.forEach(tableField -> tableNameSet.add(tableField.getValue0()));
        Map<Pair<String, String>, Field> fieldMap =repository.fetchIndexedFieldInfo(tableNameSet);
        Assert.assertTrue(fieldMap.size()>0);
    }

    @Test
    public void testChangeFieldLengthWithPK() throws SQLException {
        EtlRepository etlRepository = new EtlRepository();
        Repository repository = etlRepository.fetchOdsRepository();
        System.out.println(repository.getBizDatabase().getBizName());
        repository.alterPrimaryColumnLength("BA_DMZDK", "yljgdm", "varchar", 20);
    }
}
