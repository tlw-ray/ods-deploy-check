package com.winning.ods.deploy.app.dtsx;

import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.BizDatabase;
import com.winning.ods.deploy.domain.OrganizationCode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

/**
 * Created by tlw@winning.com.cn on 2017/6/26.
 */
public class GenerateConfigMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
//        File logFile = new File("config/ods-config-gen-log.properties");
//        FileInputStream fileInputStream = new FileInputStream(logFile);
//        LogManager.getLogManager().readConfiguration(fileInputStream);

        Logger logger = LoggerFactory.getLogger(GenerateConfigMain.class);

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        //目录检查: 清空配置文件生成路径
        File file = new File("ConfigFile/ODS/");
        if(file.exists()) {
            FileUtils.forceDelete(file);
            logger.info("删除: '" + file.getPath() + "'");
        }
        boolean mkdirs = file.mkdirs();
        logger.info("创建: '" + file.getPath() + "' " + mkdirs);


        EtlRepository etlRepository = new EtlRepository();
        BizDatabase logDatabase = etlRepository.getBizDatabase();
        String logADOConnectionString = logDatabase.getADOConnectionString();
        String logOLEDBConnectionString = logDatabase.getOLEDBConnectionString();

        Repository targetRepository = etlRepository.fetchOdsRepository();
        BizDatabase targetDatabase = targetRepository.getBizDatabase();
        String targetADOConnectionString = targetDatabase.getADOConnectionString();
        String targetOLEDBConnectionString = targetDatabase.getOLEDBConnectionString();
        //TODO 这里应该改为在数据库中实现
        targetDatabase.setName(EtlRepository.DB_TMP);
        String tempADOConnectionString = targetDatabase.getADOConnectionString();
        String tempOLEDBConnectionString = targetDatabase.getOLEDBConnectionString();


        OrganizationCode organizationCode = etlRepository.fetchOrganizationCodeList().get(0);
        String orgCode = organizationCode.getCode();

        List<Repository> sourceRepositories = etlRepository.fetchBizRepository();

        //加载配置文件模板
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File("template"));
        Template template = cfg.getTemplate("ODSConfig_.dtsConfig.ftlh");
        Map<String, String> context = new HashMap();
        context.put("orgCode", orgCode);
        context.put("targetADOConnectionString", targetADOConnectionString);
        context.put("targetOLEDBConnectionString", targetOLEDBConnectionString);
        context.put("logADOConnectionString", logADOConnectionString);
        context.put("logOLEDBConnectionString", logOLEDBConnectionString);
        context.put("tempADOConnectionString", tempADOConnectionString);
        context.put("tempOLEDBConnectionString", tempOLEDBConnectionString);

        if(sourceRepositories != null && sourceRepositories.size() > 0){
            sourceRepositories.forEach(sourceRepository -> {
                //建立配置文件模板上下文
                BizDatabase sourceDatabase = sourceRepository.getBizDatabase();
                String bizName = sourceDatabase.getBizName();
                String sourceADOConnectionString = sourceDatabase.getADOConnectionString();
                String sourceOLEDBConnectionString = sourceDatabase.getOLEDBConnectionString();
                context.put("bizName", bizName);
                context.put("sourceADOConnectionString", sourceADOConnectionString);
                context.put("sourceOLEDBConnectionString", sourceOLEDBConnectionString);
                //生成模板文件名
                String configFilePath = "ConfigFile/ODS/ODSConfig_" + bizName + ".dtsConfig";
                //生成模板
                try {
                    template.process(context, new FileWriter(configFilePath));
                    logger.info("生成配置文件: " + configFilePath);
                } catch (Exception e) {
                    logger.warn("生成配置文件异常: " + e.getMessage(), e);
                }
            });
        }else{
            logger.warn("没有能够从管理库获得任何业务系统数据库配置.");
        }
    }
}
