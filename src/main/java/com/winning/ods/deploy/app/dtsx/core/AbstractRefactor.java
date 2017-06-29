package com.winning.ods.deploy.app.dtsx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tlw@winning.com.cn on 2017/6/23.
 */
public abstract class AbstractRefactor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    //输入
    protected String content;

    Map<String ,String> replaceTaskMap;     //将要替换内容的计划

    public String process(){
        replaceTaskMap = new HashMap();

        findReplacement();

        String targetContent = content;

        for(Map.Entry<String, String> replaceEntry : replaceTaskMap.entrySet()){
            targetContent = targetContent.replace(replaceEntry.getKey(), replaceEntry.getValue());
        }

        return targetContent;
    }

    abstract void findReplacement();

    public void setContent(String content) {
        this.content = content;
    }

    protected void log(String replaceFrom, String replaceTo) {
        logger.debug("替换: " + replaceFrom + "\t 为: " + replaceTo);
    }

}
