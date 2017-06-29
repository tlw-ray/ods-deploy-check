package com.winning.ods.deploy.app.dtsx;

import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import com.winning.ods.deploy.app.dtsx.service.RefactorFieldLengthService;
import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by tlw@winning.com.cn on 2017/6/20.
 * 对DTSX进行重构
 *  1. 修改DTSX中的字段长度
 *  2. 修改ODS表的字段长度
 */
public class RefactorFieldLengthMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        EtlRepository etlRepository = new EtlRepository();
        Repository odsRepository = etlRepository.fetchOdsRepository();
        List<Repository> bizRepositories = etlRepository.fetchBizRepository();

        TableFileMapping tableFileMapping = new TableFileMapping();
        tableFileMapping.init();

        //遍历业务系统
        if(bizRepositories != null && bizRepositories.size() > 0) {
            bizRepositories.forEach(bizRepository -> {
                try {
                    RefactorFieldLengthService service = new RefactorFieldLengthService();
                    service.setTableFileMapping(tableFileMapping);
                    service.setEtlRepository(etlRepository);
                    service.setOdsRepository(odsRepository);
                    service.setBizRepository(bizRepository);
                    service.process();
                } catch (SQLException e) {
                    //日志处理
                    e.printStackTrace();
                }
            });
        }else{
            Logger logger = LoggerFactory.getLogger(RefactorFieldLengthMain.class);
            logger.warn("没有能够从管理库获得任何业务系统数据库配置.");
        }
    }
}
