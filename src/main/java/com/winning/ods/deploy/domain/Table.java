package com.winning.ods.deploy.domain;

/**
 * Created by tlw@winning.com.cn on 2017/6/8.
 */
public class Table {

//    CM_DB_TABLE_CONFIG
//    tableName:code
//    databaseName:dbname
//    fieldName:incremode
    public static final String TABLE = "CM_DB_TABLE_CONFIG";
    public static final String FIELD_TABLE_NAME = "code";               //ODS表名
    public static final String FIELD_BIZ_TABLE_NAME = "sourcecode";     //业务系统表名
    public static final String FIELD_DATABASE_NAME = "dbname";
    public static final String FIELD_TIMESTAMP_FIELD_NAME = "incremode";

    private Long id;
    private String name;
    private String description;
    private String databaseName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
