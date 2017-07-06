package com.winning.ods.deploy.app.dtsx.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/7/4.
 */
public class UseMatcherReplace {
    public static void main(String[] args){
        String tmp = "aaaaa11bb11bbb";
        Pattern pattern = Pattern.compile("11");
        Matcher matcher = pattern.matcher(tmp);
        StringBuffer stringBuffer = new StringBuffer();
        matcher.find();
        matcher = matcher.appendReplacement(stringBuffer, "3333");
        matcher.find();
        matcher = matcher.appendReplacement(stringBuffer, "4444");
        matcher.appendTail(stringBuffer);
        System.out.println(stringBuffer);
    }
}
