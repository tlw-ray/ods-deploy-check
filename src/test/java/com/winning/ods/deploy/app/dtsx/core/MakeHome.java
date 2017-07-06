package com.winning.ods.deploy.app.dtsx.core;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * Created by tlw@winning.com.cn on 2017/7/4.
 */
public class MakeHome {
    public static void main(String[] args){
        File dtsxHome = new File("config/dtsxHome.xml");
        JAXB.marshal("../HOSPITAL_DW", dtsxHome);
        String dtsxHomeString = JAXB.unmarshal(dtsxHome, String.class);
        System.out.println(dtsxHomeString);
    }
}
