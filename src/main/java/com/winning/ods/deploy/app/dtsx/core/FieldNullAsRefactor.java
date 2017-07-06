package com.winning.ods.deploy.app.dtsx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/6/22.
 */
public class FieldNullAsRefactor extends FileRefactor{

    static String asNullFieldPatternTemplate  = "[\\s,]NULL\\s+(as\\s+)?((\\Q<fieldName>\\E)|(\\Q[<fieldName>]\\E))[\\s,]";
//    static String asNullFieldPatternTemplate = "NULL[ \t]+(as[ \t]+)?\\[?<fieldName>\\]?";
//    static String fieldAsFieldPatternTemplate = "[, \t\n]+\\[?<fieldName>\\]?[, \t\n]+(as[ \t]+)?\\W?<fieldName>\\W+";
    static String fieldAsFieldPatternTemplate = "[\\s,]((\\Q<fieldName>\\E)|(\\Q[<fieldName>]\\E))\\s+(as\\s+)?((\\Q<fieldName>\\E)|(\\Q[<fieldName>]\\E))[\\s,]";

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected String tableName;
    protected Set<String> fieldNameSet;

    public String doReplace(String content) {
        String refactoredContent = content;

        logger.debug("修改表'{}'的字段'{}'为'NULL as ...'", tableName, Arrays.toString(fieldNameSet.toArray()));

        //处理<component>形式的, 主要处于ODS
        String patternString = "<component .*componentClassID=\"\\{BCEFE59B-6819-47F7-A125-63753B33ABB7\\}\"[\\w\\W]+?</component>";
        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(content);
        logger.debug("寻找组件: " + patternString);
        while(matcher.find()){
            logger.debug("找到组件: {}", patternString);
            String componentContent = matcher.group();
            String replaceContent = replaceComponent(componentContent);
            if(replaceContent!=null){
                refactoredContent = refactoredContent.replace(componentContent, replaceContent);
            }
        }

        //处理<property>形式的主要处于CDR中
        //判断如果是CDR的模式
        String propertyPatternString = "<DTS:Property DTS:Name=\\\"Expression\\\">\\\"SELECT \\[\\_\\_\\$start_lsn\\][\\w\\W]*?FROM ";
        Pattern propertyPattern = Pattern.compile(propertyPatternString, Pattern.CASE_INSENSITIVE);
        Matcher propertyMatcher = propertyPattern.matcher(refactoredContent);
        while(propertyMatcher.find()){
            String selectContent = propertyMatcher.group();
            String replaceTo = replaceField(selectContent);
            refactoredContent = content.replace(selectContent, replaceTo);
        }

        return refactoredContent;
    }

    private String replaceComponent(String componentContent){
        String selectPatternTemplate = "select[\\w\\W]+?from[\\w\\W]+?\\[{tableName}\\]";// 在Select与from之间

        ST selectST = new ST(selectPatternTemplate, '{', '}');
        selectST.add("tableName", tableName);
        String selectPatternString = selectST.render();
        Pattern pattern = Pattern.compile(selectPatternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(componentContent);

        String replacedContent = componentContent;
        if(matcher.find()){
            logger.debug("找到表{}", selectPatternTemplate);
            String selectContent = matcher.group();
            String replaceContent = replaceField(selectContent);
            if(replaceContent != null){
                replacedContent = replacedContent.replace(selectContent, replaceContent);
            }else{
                logger.warn("未能替换SQL: " + selectContent);
            }
        }else{
            logger.debug("没有找到预期模式的SQL语句: " + selectPatternString);
        }

        return replacedContent;
    }

    private String replaceField(String selectContent){
        String replaceContent = selectContent;

        for(String fieldName : fieldNameSet){
            ST asNullFieldPatternST = new ST(asNullFieldPatternTemplate);
            asNullFieldPatternST.add("fieldName", fieldName);
            String asNullFieldPatternString = asNullFieldPatternST.render();
            logger.debug("regex: {}", asNullFieldPatternString);
            Pattern asNullFieldPattern = Pattern.compile(asNullFieldPatternString, Pattern.CASE_INSENSITIVE);
            Matcher asNullFieldMatcher = asNullFieldPattern.matcher(selectContent);

            //判定是否已经有[null] [fieldName]模式存在
            if(asNullFieldMatcher.find()){
                logger.info("已经具有'[NULL] [{}]'形式的字段，不做任何修改.", fieldName);
            }else{
                //判定是否有"[FIELDNAME] [fieldName]"模式存在
                ST fieldAsFieldST = new ST(fieldAsFieldPatternTemplate);
                fieldAsFieldST.add("fieldName", fieldName);
                String fieldAsFieldPatternString = fieldAsFieldST.render();
                logger.debug("regex: {}", fieldAsFieldPatternString);
                Pattern fieldAsFieldPattern = Pattern.compile(fieldAsFieldPatternString, Pattern.CASE_INSENSITIVE);
                Matcher fieldAsFieldMatcher = fieldAsFieldPattern.matcher(replaceContent);
                boolean notFound = true;
                while(fieldAsFieldMatcher.find()){
                    notFound = false;
                    //替换"[FIELDNAME] [fieldName]"为"[NULL] [fieldName]"模式
                    String replaceFrom = fieldAsFieldMatcher.group();
                    String fieldNameRegex = "\\[?" + fieldName + "\\]?";
                    Pattern fieldPattern = Pattern.compile(fieldNameRegex, Pattern.CASE_INSENSITIVE);
                    Matcher fieldMatcher = fieldPattern.matcher(replaceFrom);
                    String replaceTo = fieldMatcher.replaceFirst("NULL");
                    replaceContent = replaceContent.replace(replaceFrom, replaceTo);
                }

                if(notFound) {
                    //在没有"[FIELDNAME] [fieldName]"模式存在的情况下判定是否有"[fieldName]"模式存在
                    String fieldNameRegex = "\\W((\\Q" + fieldName + "\\E)|(\\Q[" + fieldName + "]\\E))\\W";
                    Pattern fieldPattern = Pattern.compile(fieldNameRegex, Pattern.CASE_INSENSITIVE);
                    Matcher fieldMatcher = fieldPattern.matcher(selectContent);
                    if(fieldMatcher.find()){
                        int start = fieldMatcher.start();
                        int end = fieldMatcher.end();
                        replaceContent = selectContent.substring(0, start + 1) + "NULL AS [" + fieldName + "]" + selectContent.substring(end - 1);
                    }
                }
            }
        }
        return replaceContent;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setFieldNameSet(Set<String> fieldNameSet) {
        this.fieldNameSet = fieldNameSet;
    }
}
