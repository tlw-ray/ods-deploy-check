package com.winning.ods.deploy.dao;

import com.winning.ods.deploy.domain.BizDatabase;
import com.winning.ods.deploy.domain.FieldCheckResult;
import com.winning.ods.deploy.domain.OrganizationCode;
import com.winning.ods.deploy.util.SqlServer;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class TestEtlRepository {

    EtlRepository etlRepository = new EtlRepository();

    @Before
    public void init() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    //TODO test generate JDBC connection string

    @Test
    public void testGetOdsRepository() throws SQLException {
        Repository odsRepository = etlRepository.fetchOdsRepository();
        Assert.assertNotNull(odsRepository);
    }

    @Test
    public void testGetBizRepository() throws SQLException {
        List<Repository> repositoryList = etlRepository.fetchBizRepository();
        Assert.assertTrue(repositoryList.size()>0);
    }

    @Test
    public void getConnectBizRepository() throws SQLException {
        etlRepository.fetchBizRepository().forEach(repository -> {
            BizDatabase bizDatabase = repository.getBizDatabase();
            try {
                Assert.assertTrue(SqlServer.testConnect(bizDatabase));
            } catch (Exception e) {
                Assert.fail(e.toString());
            }
        });
    }

    public static final String DB_HIS = "HIS";
    @Test
    public void testFetchDefinedTableField() throws SQLException {
        Set<Pair<String, String>> fieldSet = etlRepository.fetchDefineTableField(DB_HIS);
        Assert.assertTrue(fieldSet.size()>0);
    }

    @Test
    public void testGetOrganizationCode() throws SQLException {
        List<OrganizationCode> organizationCodeList = etlRepository.fetchOrganizationCodeList();
        Assert.assertTrue(organizationCodeList.size()>0);
    }

    @Test
    public void testInsertTimeTempResult() throws SQLException {
        FieldCheckResult fieldCheckResult1 = new FieldCheckResult();
        fieldCheckResult1.setOrgCode("0002");
        fieldCheckResult1.setSourceName("HIS");
        fieldCheckResult1.setTargetName("ODS");
        fieldCheckResult1.setTargetFieldName("timetemp");
        fieldCheckResult1.setTargetFieldType("timestamp");
        fieldCheckResult1.setTargetFieldLength(0);

        FieldCheckResult fieldCheckResult2 = new FieldCheckResult();
        fieldCheckResult2.setOrgCode("0002");
        fieldCheckResult2.setSourceName("HIS");
        fieldCheckResult2.setTargetName("ODS");
        fieldCheckResult2.setTargetFieldName("timetemp");
        fieldCheckResult2.setTargetFieldType("timestamp");
        fieldCheckResult2.setTargetFieldLength(0);

        List<FieldCheckResult> fieldCheckResultList = new ArrayList();
        fieldCheckResultList.add(fieldCheckResult1);
        fieldCheckResultList.add(fieldCheckResult2);
        etlRepository.insertFieldCompareResult(fieldCheckResultList);
    }
}
