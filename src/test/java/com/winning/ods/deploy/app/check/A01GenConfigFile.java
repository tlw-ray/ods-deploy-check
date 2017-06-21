package com.winning.ods.deploy.app.check;

import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.domain.BizDatabase;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 * IP：10.64.5.48 用户：sa 密码：king-star123
 库名：CM_DataCenter
 */
public class A01GenConfigFile {
    public static void main(String[] args){
        BizDatabase database = new BizDatabase();
        database.setServer("127.0.0.1");
        database.setPort(1433);
        database.setUserName("sa");
        database.setPassword("sa");
        database.setInstance("instance");
        database.setName("CM_DataCenter");
        JAXB.marshal(database, new File(EtlRepository.FILE_ETL_DATABASE));
    }
}
