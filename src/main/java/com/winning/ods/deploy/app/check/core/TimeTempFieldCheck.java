package com.winning.ods.deploy.app.check.core;

import org.apache.commons.lang.StringUtils;
import org.javatuples.Pair;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by tlw@winning.com.cn on 2017/6/14.
 * 检查名为timetemp的字段在业务系统指定表中的缺失情况
 */
public class TimestampFieldCheck {

    protected Set<Pair<String, String>> definedTimestampSet;             //ETL定义的表
    protected Set<Pair<String, String>> sourceTimestampSet;              //数据源字段(业务系统)

    protected TreeSet<Pair<String, String>> lackTimestamp;               //业务系统缺失的timestamp字段

    public void process() {
        lackTimestamp = new TreeSet();

        definedTimestampSet.forEach(tableField -> {
            //将表名和字段名转化为索引，即表名大写，字段名小写。用于匹配
            String tableNameIndex = tableField.getValue0();
            String fieldNameIndex = tableField.getValue1();
            if(tableNameIndex != null){
                tableNameIndex = tableNameIndex.trim().toUpperCase();
            }
            if(fieldNameIndex != null){
                fieldNameIndex = fieldNameIndex.trim().toLowerCase();
            }
            Pair<String, String> tableFieldIndex = new Pair(tableNameIndex, fieldNameIndex);
            //如果定义了timestamp字段
            if(StringUtils.isNotEmpty(tableField.getValue1())){
                //如果数据源中未包含该表该字段，则记录为缺失
                if(!sourceTimestampSet.contains(tableFieldIndex)){
                    lackTimestamp.add(tableField);
                }
            }
        });
    }

    public Set<Pair<String, String>> getLackTimeTemp() {
        return lackTimestamp;
    }

    public Set<Pair<String, String>> getDefinedTimestampSet() {
        return definedTimestampSet;
    }

    public void setDefinedTimestampSet(Set<Pair<String, String>> definedTimestampSet) {
        this.definedTimestampSet = definedTimestampSet;
    }

    public Set<Pair<String, String>> getSourceTimestampSet() {
        return sourceTimestampSet;
    }

    public void setSourceTimestampSet(Set<Pair<String, String>> sourceTimestampSet) {
        this.sourceTimestampSet = sourceTimestampSet;
    }
}
