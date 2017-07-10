package com.winning.ods.deploy.util;

import org.stringtemplate.v4.ST;

import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class SqlUtil {
    //TODO 检查ETL配置的表名不能为空, null判断， 空白判断
    public static String createStringInCondition(Set<String> stringSet){
        StringBuilder stringBuilder = new StringBuilder("(");
        stringSet.forEach(string -> {
            stringBuilder.append("'");
            stringBuilder.append(string);
            stringBuilder.append("',");
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    public static String generateAlterTableQuery(String tableName, String columnName, String dataType, int targetLength, boolean notNull){
        String ALTER_TABLE = "ALTER TABLE <tableName> ALTER COLUMN <columnName> <dataType>(<length>)";
        ST alterTableST = new ST(ALTER_TABLE);
        alterTableST.add("tableName", tableName);
        alterTableST.add("columnName", columnName);
        alterTableST.add("dataType", dataType);
        alterTableST.add("length", targetLength);
        String result = alterTableST.render();
        if(notNull){
            result += " not null";
        }
        return result;
    }
}
