package com.winning.ods.deploy.app.check.core;

import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/14.
 */
public class TestTimestampLack {

    @Test
    public void testTimestampFieldCheck(){
        Field field1 = new Field();
        field1.setTableName("T1");
        field1.setFieldName("timestamp");
        Field field2 = new Field();
        field2.setTableName("T2");
        field2.setFieldName("timestamp");

        Map<Pair<String, String>, Field> sourceFieldMap = new HashMap();
        sourceFieldMap.put(Repository.createTableFieldIndex(field1), field1);

        Map<Pair<String, String>, Field> targetFieldMap = new HashMap();
        targetFieldMap.put(Repository.createTableFieldIndex(field1), field1);
        targetFieldMap.put(Repository.createTableFieldIndex(field2), field2);

        TimestampFieldCheck timestampFieldCheck = new TimestampFieldCheck();
        timestampFieldCheck.setDefinedTimestampSet(targetFieldMap.keySet());
        timestampFieldCheck.setSourceTimestampSet(sourceFieldMap.keySet());
        timestampFieldCheck.process();
        Set<Pair<String, String>> lackTimestamp = timestampFieldCheck.getLackTimeTemp();
        Assert.assertEquals(1, lackTimestamp.size());
        Assert.assertTrue(lackTimestamp.contains(Repository.createTableFieldIndex(field2)));
    }
}
