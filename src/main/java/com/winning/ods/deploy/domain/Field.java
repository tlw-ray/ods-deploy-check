package com.winning.ods.deploy.domain;

/**
 * Created by tlw@winning.com.cn on 2017/6/7.
 * TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, ORDINAL_POSITION
 */
public class Field{

    private String tableName;
    private String fieldName;
    private String dataType;
    private Integer characterMaximumLength;
    private int ordinalPosition;

    @Override
    public String toString() {
        return "Field{" +
                "tableName='" + tableName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", characterMaximumLength=" + characterMaximumLength +
                ", ordinalPosition=" + ordinalPosition +
                '}';
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
