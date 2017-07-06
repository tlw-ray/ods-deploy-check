package com.winning.ods.deploy.app.dtsx.core;

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/6/26.
 */
public class TestFieldNullAsRefactor {

    @Test
    public void test_YY_KSBMK() throws IOException {
        String source = "test-data/fieldNullAsRefactor/YY_KSBMK.dtsx";
        String target = "test-data/fieldNullAsRefactor/YY_KSBMK.dtsx.target";
        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("zbbz");
        fieldNameSet.add("zzghbz");
        String tableName = "YY_KSBMK";
        test(source, target, tableName, fieldNameSet);
    }

    @Test
    public void test_VW_GHZDK() throws IOException {
        String source = "test-data/fieldNullAsRefactor/VW_GHZDK.dtsx";
        String target = "test-data/fieldNullAsRefactor/VW_GHZDK.dtsx.target";
        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("dzdj");
        String tableName = "VW_GHZDK";
        test(source, target, tableName, fieldNameSet);
    }

    @Test
    public void test_EMR_QTBLJLK() throws IOException {
        String source = "test-data/fieldNullAsRefactor/EMR_QTBLJLK.dtsx";
        String target = "test-data/fieldNullAsRefactor/EMR_QTBLJLK.dtsx.target";
        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("sfhlhtmb");
        String tableName = "EMR_QTBLJLK";
        test(source, target, tableName, fieldNameSet);
    }

    @Test
    public void test_CPOE_BRSYK() throws IOException {
        String source = "test-data/fieldNullAsRefactor/CPOE_BRSYK.dtsx";
        String target = "test-data/fieldNullAsRefactor/CPOE_BRSYK.dtsx.target";
        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("tsbahm");
        String tableName = "CPOE_BRSYK";
        test(source, target, tableName, fieldNameSet);
    }

    @Test
    public void test_VW_MZCFK() throws IOException {
        String source = "test-data/fieldNullAsRefactor/VW_MZCFK.dtsx";
        String target = "test-data/fieldNullAsRefactor/VW_MZCFK.dtsx.target";
        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("kd_lxdh");
        String tableName = "VW_MZCFK";
        test(source, target, tableName, fieldNameSet);
    }

    @Test
    public void testNullAsField(){
        String[] cases = new String[]{
                "select null as field from table1",
                "select null field from table1",
                "select null [field] from table1",
                "select field from table1",
                "select [field] from table1"
        };

        ST asNullFieldPatternST = new ST(FieldNullAsRefactor.asNullFieldPatternTemplate);
        asNullFieldPatternST.add("fieldName", "field");
        String asNullFieldPatternString = asNullFieldPatternST.render();
        System.out.println(asNullFieldPatternString);
        Pattern asNullFieldPattern = Pattern.compile(asNullFieldPatternString, Pattern.CASE_INSENSITIVE);

        int idx = 0;
        for(String selectContent:cases){
            Matcher asNullFieldMatcher = asNullFieldPattern.matcher(selectContent);
            boolean contains = asNullFieldMatcher.find();
            System.out.println(contains);
            if(idx++ < 3) {
                Assert.assertTrue(contains);
            }else{
                Assert.assertFalse(contains);
            }
        }
    }

    @Test
    public void testFieldAsField(){
        String[] cases = new String[]{
                "select field as field from table1",
                "select field field from table1",
                "select [field] [field] from table1",
                "select [field] as [field] from table1",
                "select field from table1",
                "select [field] from table1"
        };
        ST asNullFieldPatternST = new ST(FieldNullAsRefactor.fieldAsFieldPatternTemplate);
        asNullFieldPatternST.add("fieldName", "field");
        String asNullFieldPatternString = asNullFieldPatternST.render();
        System.out.println(asNullFieldPatternString);
        Pattern asNullFieldPattern = Pattern.compile(asNullFieldPatternString, Pattern.CASE_INSENSITIVE);

        int idx = 0;
        for(String selectContent:cases){
            Matcher asNullFieldMatcher = asNullFieldPattern.matcher(selectContent);
            boolean contains = asNullFieldMatcher.find();
            System.out.println(contains);
            if(idx++ < 4) {
                Assert.assertTrue(contains);
            }else{
                Assert.assertFalse(contains);
            }
        }
    }

    private void test(String source, String target, String tableName, Set<String> fieldNameSet) throws IOException {
        Path sourcePath = Paths.get(source);
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes, "UTF-8");

        Path targetPath = Paths.get(target);
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes, "UTF-8");

        FieldNullAsRefactor fieldNullAsRefactor = new FieldNullAsRefactor();
        fieldNullAsRefactor.setTableName(tableName);
        fieldNullAsRefactor.setFieldNameSet(fieldNameSet);
        String refactoredContent = fieldNullAsRefactor.doReplace(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
    }
}
