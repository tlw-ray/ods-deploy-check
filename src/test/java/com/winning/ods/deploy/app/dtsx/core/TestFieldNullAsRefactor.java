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
public class TestRefactorNullAsField {
    @Test
    public void test_YY_KSBMK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorNullAsField/YY_KSBMK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/refactorNullAsField/YY_KSBMK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("zbbz");
        fieldNameSet.add("zzghbz");

        RefactorNullAsField refactorNullAsField = new RefactorNullAsField();
        refactorNullAsField.setTableName("YY_KSBMK");
        refactorNullAsField.setFieldNameSet(fieldNameSet);
        String refactoredContent = refactorNullAsField.refactor(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
    }

    @Test
    public void test_VW_GHZDK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorNullAsField/VW_GHZDK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/refactorNullAsField/VW_GHZDK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("dzdj");

        RefactorNullAsField refactorNullAsField = new RefactorNullAsField();
        refactorNullAsField.setTableName("VW_GHZDK");
        refactorNullAsField.setFieldNameSet(fieldNameSet);
        String refactoredContent = refactorNullAsField.refactor(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
    }

    @Test
    public void test_EMR_QTBLJLK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorNullAsField/EMR_QTBLJLK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/refactorNullAsField/EMR_QTBLJLK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("sfhlhtmb");

        RefactorNullAsField refactorNullAsField = new RefactorNullAsField();
        refactorNullAsField.setTableName("EMR_QTBLJLK");
        refactorNullAsField.setFieldNameSet(fieldNameSet);
        String refactoredContent = refactorNullAsField.refactor(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
    }

    @Test
    public void test_CPOE_BRSYK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorNullAsField/CPOE_BRSYK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/refactorNullAsField/CPOE_BRSYK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("tsbahm");

        RefactorNullAsField refactorNullAsField = new RefactorNullAsField();
        refactorNullAsField.setTableName("CPOE_BRSYK");
        refactorNullAsField.setFieldNameSet(fieldNameSet);
        String refactoredContent = refactorNullAsField.refactor(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
    }

    @Test
    public void test_VW_MZCFK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorNullAsField/VW_MZCFK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/refactorNullAsField/VW_MZCFK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        Set<String> fieldNameSet = new HashSet();
        fieldNameSet.add("kd_lxdh");

        RefactorNullAsField refactorNullAsField = new RefactorNullAsField();
        refactorNullAsField.setTableName("VW_MZCFK");
        refactorNullAsField.setFieldNameSet(fieldNameSet);
        String refactoredContent = refactorNullAsField.refactor(sourceContent);

        Assert.assertEquals(targetContent, refactoredContent);
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

        ST asNullFieldPatternST = new ST(RefactorNullAsField.asNullFieldPatternTemplate);
        asNullFieldPatternST.add("fieldName", Pattern.quote("field"));
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
        ST asNullFieldPatternST = new ST(RefactorNullAsField.fieldAsFieldPatternTemplate);
        asNullFieldPatternST.add("fieldName", Pattern.quote("field"));
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

}
