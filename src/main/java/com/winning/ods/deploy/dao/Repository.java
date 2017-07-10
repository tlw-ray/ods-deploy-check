package com.winning.ods.deploy.dao;

import com.winning.ods.deploy.domain.BizDatabase;
import com.winning.ods.deploy.domain.Field;
import com.winning.ods.deploy.util.SqlUtil;
import org.apache.commons.lang.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.sql.*;
import java.util.*;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */

public class Repository {

    public static Pair<String, String> createTableFieldIndex(Field field){
        //建立不区分大小写的Table和Field的复合索引
        String tableNameIndex = null;
        String fieldNameIndex = null;
        if(field.getTableName() != null){
            tableNameIndex = field.getTableName().trim().toUpperCase();
        }
        if(field.getFieldName() != null){
            fieldNameIndex = field.getFieldName().trim().toLowerCase();
        }
        return new Pair(tableNameIndex, fieldNameIndex);
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected BizDatabase bizDatabase;

    public Map<Pair<String, String>, Field> fetchIndexedFieldInfo(Set<String> tableNames) throws SQLException {
        Map<Pair<String, String>, Field> result = new HashMap();
        if(tableNames.size()>0) {
            String connectionStr = getBizDatabase().getJdbcConnectionString();
            try(Connection connection = DriverManager.getConnection(connectionStr)){
                String tableNameConditionStr = SqlUtil.createStringInCondition(tableNames);
                String query = "select TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, ORDINAL_POSITION from " + bizDatabase.getName() + ".INFORMATION_SCHEMA.COLUMNS where TABLE_NAME in " + tableNameConditionStr;
                logger.debug(query);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    String tableName = resultSet.getString(1);
                    String fieldName = resultSet.getString(2);

                    String dataType = resultSet.getString(3);
                    int characterMaximumLength = resultSet.getInt(4);
                    int ordinalPosition = resultSet.getInt(5);

                    Field field = new Field();
                    field.setTableName(tableName);
                    field.setFieldName(fieldName);
                    field.setDataType(dataType);
                    field.setCharacterMaximumLength(characterMaximumLength);
                    field.setOrdinalPosition(ordinalPosition);
                    Pair<String, String> tableFieldIndex = createTableFieldIndex(field);
                    if(result.containsKey(tableFieldIndex)){
                        Field field0 = result.get(tableFieldIndex);
                        logger.error("大小写重叠的表和字段名：\n\t[" + bizDatabase.getName() + "." + field0.getTableName() + "." + field0.getFieldName() + "]\n\t[" + bizDatabase.getName() + "." + field.getTableName() + "." + field.getFieldName() + "]");
                    }else{
                        result.put(tableFieldIndex, field);
                    }
                }
            }
        }
        return result;
    }

    public boolean hasRow(String tableName) throws SQLException {
        String connectionStr = bizDatabase.getJdbcConnectionString();
        logger.debug(connectionStr);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            String query = "SELECT TOP 1 1 FROM " + tableName;
            logger.debug(query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return resultSet.next();
        }
    }

    public boolean executeQuery(String sql) throws SQLException {
        String connectionStr = bizDatabase.getJdbcConnectionString();
        logger.debug(connectionStr);
        try(Connection connection = DriverManager.getConnection(connectionStr)){
            logger.debug(sql);
            return connection.createStatement().execute(sql);
        }
    }

    public void alterPrimaryColumnLength(String tableName, String fieldName, String dataType, int targetLength){
        //查询表的主键名
        String primaryKeyQuery = "select CONSTRAINT_NAME from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where TABLE_NAME = ? and CONSTRAINT_TYPE = 'PRIMARY KEY'";
        //根据主键名查询该主键的字段构成
        String columnsQuery = "select COLUMN_NAME from INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE where CONSTRAINT_NAME = ?";
        //删除主键
        String deletePrimaryKeyQuery = "ALTER TABLE <tableName> DROP CONSTRAINT <constraintName>";
        //修改字段长度
        String alterTableQuery = SqlUtil.generateAlterTableQuery(tableName, fieldName, dataType, targetLength, true);
        String connectionStr = bizDatabase.getJdbcConnectionString();
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(connectionStr);
            connection.setAutoCommit(false);
            logger.debug("set auto commit false;");
            //取该表主键
            logger.debug("执行: {}, 表名: {}", primaryKeyQuery + "\t" + tableName);
            PreparedStatement primaryKeyPreparedStatement = connection.prepareStatement(primaryKeyQuery);
            primaryKeyPreparedStatement.setString(1, tableName);
            ResultSet primaryKeyNameResultSet = primaryKeyPreparedStatement.executeQuery();
            //如果该表有主键
            if(primaryKeyNameResultSet.next()){
                String primaryKeyName = primaryKeyNameResultSet.getString(1);
                if(StringUtils.isNotEmpty(primaryKeyName)){
                    //取该主键是由哪些字段构成
                    logger.debug("执行: {}, 约束名: {}", columnsQuery, primaryKeyName);
                    PreparedStatement preparedStatement = connection.prepareStatement(columnsQuery);
                    preparedStatement.setString(1, primaryKeyName);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    Set<String> columnNameSet = new HashSet();
                    while(resultSet.next()){
                        String columnName = resultSet.getString(1);
                        columnNameSet.add(columnName);
                    }

                    //如果要修改长度的字段是主键的构成
                    if(columnNameSet.contains(fieldName)){
                        //删除主键
                        ST st = new ST(deletePrimaryKeyQuery);
                        st.add("tableName", tableName);
                        st.add("constraintName", primaryKeyName);
                        String query = st.render();
                        logger.debug(query);
                        connection.createStatement().execute(query);

                        //修改字段长度
                        logger.debug(alterTableQuery);
                        connection.createStatement().execute(alterTableQuery);

                        //重新建立主键
                        StringBuilder createPrimaryKeyBuilder = new StringBuilder("alter table [");
                        createPrimaryKeyBuilder.append(tableName);
                        createPrimaryKeyBuilder.append("] add constraint ");
                        createPrimaryKeyBuilder.append(primaryKeyName);
                        createPrimaryKeyBuilder.append(" primary key(");
                        for(String columnName : columnNameSet){
                            createPrimaryKeyBuilder.append("[");
                            createPrimaryKeyBuilder.append(columnName);
                            createPrimaryKeyBuilder.append("],");
                        }
                        createPrimaryKeyBuilder.deleteCharAt(createPrimaryKeyBuilder.length() - 1);
                        createPrimaryKeyBuilder.append(")");
                        String createPrimaryKeyQuery = createPrimaryKeyBuilder.toString();
                        logger.debug(createPrimaryKeyQuery);
                        connection.createStatement().execute(createPrimaryKeyQuery);
                        return;
                    }
                }
            }
            //如果不是主键构成则修改字段长度
            logger.debug(alterTableQuery);
            connection.createStatement().execute(alterTableQuery);
        }catch(SQLException e){
            if(connection != null){
                try {
                    logger.warn("修改主键长度失败回滚: " + e.getMessage());
                    connection.rollback();
                    e.printStackTrace();
                } catch (SQLException ex) {
                    logger.warn("修改主键长度失败回滚失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }finally{
            if(connection != null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                }catch(SQLException e){
                    logger.warn("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }

    public BizDatabase getBizDatabase() {
        return bizDatabase;
    }

    public void setBizDatabase(BizDatabase bizDatabase) {
        this.bizDatabase = bizDatabase;
    }
}
