package com.winning.ods.deploy.domain;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class BizDatabase extends Database{

    protected String bizName;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    @Override
    public String toString() {
        return "BizDatabase{" +
                "bizName='" + bizName + '\'' +
                "} " + super.toString();
    }
}
