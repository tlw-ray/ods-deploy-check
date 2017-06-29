package com.winning.ods.deploy.app;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/6/27.
 */
public class LogDebug {
    Logger logger = LoggerFactory.getLogger(getClass());
    @BeforeClass
    public static void beforeClass(){
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
    @Test
    public void logDebug(){
        logger.info("info msg");
        logger.debug("debug msg");
        logger.trace("trace msg");
        logger.warn("warn is {}, {}", "AAA", "BBB");
    }

    @Test
    public void testReplace(){
        String string = "this is a dog";    //TODO replace a as abc
        String string1 = "this has a dog";
        String string2 = "this not a dog";
        String regex = " (i|ha)s ";
        Pattern pattern = Pattern.compile(regex);
        System.out.println(pattern.matcher(string).find());
        System.out.println(pattern.matcher(string1).find());
        System.out.println(pattern.matcher(string2).find());

        System.out.println(string.replace(regex, "aaaa"));
        System.out.println(string1.replace(regex, "aaaa"));
    }

    @Test
    public void testRegexEscape(){
        String fieldName = "a+field";//其中下划线是正则关键字
        String sql = "select a+field from table";
        System.out.println(Pattern.quote(fieldName));

        Pattern pattern01 = Pattern.compile(fieldName);
        Pattern pattern02 = Pattern.compile(Pattern.quote(fieldName));
        System.out.println(pattern01.matcher(sql).find());
        System.out.println(pattern02.matcher(sql).find());
    }

}
