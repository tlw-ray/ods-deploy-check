package com.winning.ods.deploy.dao;

import com.winning.ods.deploy.domain.BizDatabase;
import com.winning.ods.deploy.domain.Field;
import com.winning.ods.deploy.util.SqlUtil;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public BizDatabase getBizDatabase() {
        return bizDatabase;
    }

    public void setBizDatabase(BizDatabase bizDatabase) {
        this.bizDatabase = bizDatabase;
    }
}
