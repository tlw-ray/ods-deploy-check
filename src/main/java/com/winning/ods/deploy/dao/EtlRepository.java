package com.winning.ods.deploy.dao;

import com.winning.ods.deploy.domain.*;
import com.winning.ods.deploy.util.SqlUtil;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import javax.xml.bind.JAXB;
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class EtlRepository extends Repository{

    public static final String FILE_ETL_DATABASE = "config/etlDatabase.xml";

    Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DB_ETL = "CM_DataCenter";
    public static final String DB_ODS = "ODS";
    public static final String DB_CDR = "CDR";
    public static final String DB_ODR = "ODR";
    public static final String DB_TMP = "CM_Temporary";

    public EtlRepository(){
        bizDatabase = JAXB.unmarshal(new File(FILE_ETL_DATABASE), BizDatabase.class);
        bizDatabase.setBizName(DB_ETL);
    }

    public Repository fetchOdsRepository() throws SQLException {
        String query = "select <server>, <port>, <userName>, <password>, <instance>, <name> from <table> where <bizCode> = '<ods>'";
        ST st = new ST(query);
        st.add("server", Database.FIELD_SERVER);
        st.add("port", Database.FIELD_PORT);
        st.add("userName", Database.FIELD_USER_NAME);
        st.add("password", Database.FIELD_PASSWORD);
        st.add("instance", Database.FIELD_INSTANCE);
        st.add("name", Database.FIELD_NAME);
        st.add("bizCode", Database.FIELD_BIZ_CODE);
        st.add("table", Database.TABLE);
        st.add("ods", DB_ODS);
        String sql = st.render();
        String connectionStr = getBizDatabase().getJdbcConnectionString();
        logger.info(connectionStr);
        logger.info(sql);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            if(resultSet.next()){
                BizDatabase odsDatabase = new BizDatabase();
                odsDatabase.setServer(resultSet.getString(1));
                String portStr = resultSet.getString(2);
                if(StringUtils.isNotEmpty(portStr)){
                    odsDatabase.setPort(Integer.parseInt(portStr));
                }
                odsDatabase.setUserName(resultSet.getString(3));
                odsDatabase.setPassword(resultSet.getString(4));
                odsDatabase.setInstance(resultSet.getString(5));
                odsDatabase.setBizName(DB_ODS);
                odsDatabase.setName(resultSet.getString(6));
                logger.info(odsDatabase.toString());
                Repository odsRepository = new Repository();
                odsRepository.setBizDatabase(odsDatabase);
                return odsRepository;
            }else{
                throw new RuntimeException("无法获得ODS库连接信息.");
            }
        }
    }

    public List<Repository> fetchBizRepository() throws SQLException {
        Set<String> platformDatabaseSet = new HashSet();
        platformDatabaseSet.add(DB_ODR);
        platformDatabaseSet.add(DB_CDR);
        platformDatabaseSet.add(DB_ETL);
        platformDatabaseSet.add(DB_ODS);
        platformDatabaseSet.add(DB_TMP);

        String query = "select <server>, <port>, <userName>, <password>, <instance>, <name>, <bizCode> from <table> where <bizCode> not in <platformDatabases>";
        ST st = new ST(query);
        st.add("server", Database.FIELD_SERVER);
        st.add("port", Database.FIELD_PORT);
        st.add("userName", Database.FIELD_USER_NAME);
        st.add("password", Database.FIELD_PASSWORD);
        st.add("instance", Database.FIELD_INSTANCE);
        st.add("name", Database.FIELD_NAME);
        st.add("bizCode", Database.FIELD_BIZ_CODE);
        st.add("table", Database.TABLE);
        st.add("platformDatabases", SqlUtil.createStringInCondition(platformDatabaseSet));
        String sql = st.render();
        String connectionStr = getBizDatabase().getJdbcConnectionString();
        logger.info(connectionStr);
        logger.info(sql);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            List<Repository> result = new ArrayList();
            while(resultSet.next()){
                BizDatabase bizDatabase = new BizDatabase();
                bizDatabase.setServer(resultSet.getString(1));
                String portStr = resultSet.getString(2);
                if(StringUtils.isNotEmpty(portStr)){
                    bizDatabase.setPort(Integer.parseInt(portStr));
                }
                bizDatabase.setUserName(resultSet.getString(3));
                bizDatabase.setPassword(resultSet.getString(4));
                bizDatabase.setInstance(resultSet.getString(5));
                bizDatabase.setName(resultSet.getString(6));
                bizDatabase.setBizName(resultSet.getString(7));
                logger.info(bizDatabase.toString());
                Repository bizRepository = new Repository();
                bizRepository.setBizDatabase(bizDatabase);
                result.add(bizRepository);
            }
            logger.info("count: " + result.size());
            return result;
        }
    }

    //取ETL中定义的ODS表，有的表带有时间戳字段名
    public Set<Pair<String, String>> fetchDefineTableField(String bizDatabaseName) throws SQLException {
        Set<Pair<String, String>> defineFields = new HashSet();

        ST st = new ST("select <tableName>, <timestampFieldName> from <table> where <databaseName> = '<bizDatabaseName>'");
        //如果是ODS库则使用code字段，如果是业务库则使用sourcecode字段
        st.add("tableName", Table.FIELD_TABLE_NAME);
        st.add("timestampFieldName", Table.FIELD_TIMESTAMP_FIELD_NAME);
        st.add("table", Table.TABLE);
        st.add("databaseName", Table.FIELD_DATABASE_NAME);
        st.add("bizDatabaseName", bizDatabaseName);
        String sql = st.render();
        logger.info(sql);
        try(Connection connection = DriverManager.getConnection(getBizDatabase().getJdbcConnectionString())){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String tableName = resultSet.getString(1);
                String fieldName = resultSet.getString(2);

                Pair<String, String> tableField = new Pair(tableName, fieldName);
                defineFields.add(tableField);
            }
            logger.info("count: " + defineFields.size());
            return defineFields;
        }
    }

    public List<OrganizationCode> fetchOrganizationCodeList() throws SQLException {
        String connectionStr = bizDatabase.getJdbcConnectionString();
        String sql = "select code, name, parentid from [CM_ORGENIZATION]";
        logger.info(connectionStr);
        logger.info(sql);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            List<OrganizationCode> organizationCodeList = new ArrayList();
            while(resultSet.next()){
                String code = resultSet.getString(1);
                String name = resultSet.getString(2);
                Long parentId = resultSet.getLong(3);

                OrganizationCode organizationCode = new OrganizationCode();
                organizationCode.setCode(code);
                organizationCode.setName(name);
                organizationCode.setParentId(parentId);
                organizationCodeList.add(organizationCode);
            }
            return organizationCodeList;
        }
    }

    public int[] insertFieldCompareResult(List<FieldCheckResult> fieldCheckResultList) throws SQLException {
        String connectionStr = bizDatabase.getJdbcConnectionString();
        String sql = "insert into [CM_DW_SOURCE_STATUS] (orgcode, sourcename, destinationname, " +
                "dest_tablename, dest_colname, dest_coldatatype, dest_collength, dest_colprecision, " +
                "sour_coldatatype, sour_collength, sour_colprecision," +
                "lsnid, infocode, updatetime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        logger.info(connectionStr);
        logger.info(sql);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for(FieldCheckResult fieldCheckResult : fieldCheckResultList){
                preparedStatement.setString(1, fieldCheckResult.getOrgCode());
                preparedStatement.setString(2, fieldCheckResult.getSourceName());
                preparedStatement.setString(3, fieldCheckResult.getTargetName());
                preparedStatement.setString(4, fieldCheckResult.getTargetTableName());
                preparedStatement.setString(5, fieldCheckResult.getTargetFieldName());
                preparedStatement.setString(6, fieldCheckResult.getTargetFieldType());
                preparedStatement.setObject(7, fieldCheckResult.getTargetFieldLength());
                preparedStatement.setObject(8, fieldCheckResult.getTargetFieldPrecision());
                preparedStatement.setString(9, fieldCheckResult.getSourceFieldType());
                preparedStatement.setObject(10, fieldCheckResult.getSourceFieldLength());
                preparedStatement.setObject(11, fieldCheckResult.getSourceFieldPrecision());
                preparedStatement.setObject(12, fieldCheckResult.getLsnId());
                preparedStatement.setString(13, fieldCheckResult.getInformationCode());
                preparedStatement.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
                preparedStatement.addBatch();
            }
            return preparedStatement.executeBatch();
        }
    }

}
