package com.winning.ods.deploy.app.check.core;

import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by tlw@winning.com.cn on 2017/6/12.
 */
public class TestFieldChecker {
    @Test
    public void testFieldCheckerLack(){
        Field field0 = new Field();
        field0.setTableName("T_0");
        field0.setFieldName("F_0");
        field0.setDataType("FT_0");

        Field field1 = new Field();
        field1.setTableName("T_1");
        field1.setFieldName("F_1");
        field1.setDataType("FT_1");
        field1.setCharacterMaximumLength(1);

        Map<Pair<String, String>, Field> definedFieldMap = new HashMap();
        definedFieldMap.put(new Pair(field0.getTableName().toUpperCase(), field0.getFieldName().toLowerCase()), field0);
        definedFieldMap.put(new Pair(field1.getTableName().toUpperCase(), field1.getFieldName().toLowerCase()), field1);
        Map<Pair<String, String>, Field> checkFieldMap = new HashMap();

        FieldChecker fieldChecker = new FieldChecker();
        fieldChecker.setTargetFieldMap(definedFieldMap);
        fieldChecker.setSourceFieldMap(checkFieldMap);
        fieldChecker.process();
        Assert.assertEquals(fieldChecker.getMissingFieldSet(), definedFieldMap.keySet());
        Assert.assertEquals(fieldChecker.getUndefinedFieldSet(), checkFieldMap.keySet());
        Assert.assertEquals(fieldChecker.getTypeConflictFieldSet(), new TreeSet<Pair<String, String>>());
        Assert.assertEquals(fieldChecker.getLengthConflictFieldSet(), new TreeSet<Pair<String, String>>());
    }

    @Test
    public void testFieldChecker(){
        Field field0 = new Field();
        field0.setTableName("T_0");
        field0.setFieldName("F_0");
        field0.setDataType("FT_0");

        Field field0_ = new Field();
        field0_.setTableName("T_0");
        field0_.setFieldName("F_0");
        field0_.setDataType("FT_0");

        Field field1 = new Field();
        field1.setTableName("T_1");
        field1.setFieldName("F_1");
        field1.setDataType("FT_1");

        Field field1_ = new Field();
        field1_.setTableName("T_1");
        field1_.setFieldName("F_1");
        field1_.setDataType("FT_1");
        field1_.setCharacterMaximumLength(1);

        Field field2 = new Field();
        field2.setTableName("T_2");
        field2.setFieldName("F_2");
        field2.setDataType("FT_2");
        field2.setCharacterMaximumLength(2);

        Field field2_ = new Field();
        field2_.setTableName("T_2");
        field2_.setFieldName("F_2");
        field2_.setDataType("FT_2");
        field2_.setCharacterMaximumLength(23);

        Field field3 = new Field();
        field3.setTableName("T_3");
        field3.setFieldName("F_3");
        field3.setDataType("FT_3");
        field3.setCharacterMaximumLength(3);

        Field field3_ = new Field();
        field3_.setTableName("T_3");
        field3_.setFieldName("F_3");
        field3_.setDataType("FT_3_");
        field3_.setCharacterMaximumLength(3);

        Field field4 = new Field();
        field4.setTableName("T_4");
        field4.setFieldName("F_4");
        field4.setDataType("FT_4");
        field4.setCharacterMaximumLength(4);

        Field field4_ = new Field();
        field4_.setTableName("T_0");
        field4_.setFieldName("F_4");
        field4_.setDataType("FT_4_");

        Field field5_ = new Field();
        field5_.setTableName("T_5");
        field5_.setFieldName("F_5");
        field5_.setDataType("FT_5");
        field5_.setCharacterMaximumLength(4);


        Map<Pair<String, String>, Field> definedFieldMap = new HashMap();
        definedFieldMap.put(Repository.createTableFieldIndex(field0), field0);
        definedFieldMap.put(Repository.createTableFieldIndex(field1), field1);
        definedFieldMap.put(Repository.createTableFieldIndex(field2), field2);
        definedFieldMap.put(Repository.createTableFieldIndex(field3), field3);
        definedFieldMap.put(Repository.createTableFieldIndex(field4), field4);
        Map<Pair<String, String>, Field> checkFieldMap = new HashMap();
        checkFieldMap.put(Repository.createTableFieldIndex(field0_), field0_);
        checkFieldMap.put(Repository.createTableFieldIndex(field1_), field1_);
        checkFieldMap.put(Repository.createTableFieldIndex(field2_), field2_);
        checkFieldMap.put(Repository.createTableFieldIndex(field3_), field3_);
        checkFieldMap.put(Repository.createTableFieldIndex(field4_), field4_);
        checkFieldMap.put(Repository.createTableFieldIndex(field5_), field5_);

        FieldChecker fieldChecker = new FieldChecker();
        fieldChecker.setTargetFieldMap(definedFieldMap);
        fieldChecker.setSourceFieldMap(checkFieldMap);
        fieldChecker.process();

        TreeSet<Pair<String, String>> missingFieldSet = new TreeSet();
        missingFieldSet.add(Repository.createTableFieldIndex(field4));
        TreeSet<Pair<String, String>> typeConflictFieldSet = new TreeSet();
        typeConflictFieldSet.add(Repository.createTableFieldIndex(field3));
        TreeSet<Pair<String, String>> lengthConflictFieldSet = new TreeSet();
        lengthConflictFieldSet.add(Repository.createTableFieldIndex(field1));
        lengthConflictFieldSet.add(Repository.createTableFieldIndex(field2));
        TreeSet<Pair<String, String>> undefinedFieldSet = new TreeSet();
        undefinedFieldSet.add(Repository.createTableFieldIndex(field4_));
        undefinedFieldSet.add(Repository.createTableFieldIndex(field5_));

        Assert.assertEquals(missingFieldSet, fieldChecker.getMissingFieldSet());
        Assert.assertEquals(undefinedFieldSet, fieldChecker.getUndefinedFieldSet());
        Assert.assertEquals(typeConflictFieldSet, fieldChecker.getTypeConflictFieldSet());
        Assert.assertEquals(lengthConflictFieldSet, fieldChecker.getLengthConflictFieldSet());
    }

}
