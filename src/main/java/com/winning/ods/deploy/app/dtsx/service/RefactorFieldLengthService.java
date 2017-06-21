package com.winning.ods.deploy.app.dtsx.service;

import com.winning.ods.deploy.app.check.core.FieldChecker;
import com.winning.ods.deploy.app.dtsx.core.ReplaceFieldLength;
import com.winning.ods.deploy.app.dtsx.core.TableAlter;
import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/20.
 * 对单个表的字段长度进行重构
 */
public class RefactorFieldLengthService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EtlRepository etlRepository;
    private Repository odsRepository;
    private Repository bizRepository;

    private TableFileMapping tableFileMapping;

    public void process() throws SQLException {

        String bizName = bizRepository.getBizDatabase().getBizName();

        //取数据
        Set<Pair<String, String>> definedTableFieldSet = etlRepository.fetchDefineTableField(bizName);
        Set<String> tableNameSet = new HashSet();
        definedTableFieldSet.forEach(tableField -> tableNameSet.add(tableField.getValue0()));
        Map<Pair<String, String>, Field> sourceFieldMap = bizRepository.fetchIndexedFieldInfo(tableNameSet);
        Map<Pair<String, String>, Field> targetFieldMap = odsRepository.fetchIndexedFieldInfo(tableNameSet);

        //进行字段长度检查
        FieldChecker fieldChecker = new FieldChecker();
        fieldChecker.setSourceFieldMap(sourceFieldMap);
        fieldChecker.setTargetFieldMap(targetFieldMap);
        fieldChecker.process();
        fieldChecker.getLengthConflictFieldSet().forEach(tableField -> {
            Field odsField = targetFieldMap.get(tableField);
            Field bizField = sourceFieldMap.get(tableField);
            if(odsField != null){
                String tableKey = odsField.getTableName().trim().toUpperCase();//表名用来对应*.dtsx文件名，采用大写是约定
                String odsTableName = odsField.getTableName();
                String odsFieldName = odsField.getFieldName();
                String dataType = odsField.getDataType();
                int odsFieldLength = odsField.getCharacterMaximumLength();
                int bizFieldLength = bizField.getCharacterMaximumLength();

                ST infoST = new ST("对业务系统'<bizName>': 转换ODS表'<odsTableName>'的'<dataType>'类型字段'<odsFieldName>'的长度由'<odsFieldLength>'到'<bizFieldLength>'...");
                infoST.add("bizName", bizName);
                infoST.add("odsTableName", odsTableName);
                infoST.add("dataType", dataType);
                infoST.add("odsFieldName", odsFieldName);
                infoST.add("odsFieldLength", odsFieldLength);
                infoST.add("bizFieldLength", bizFieldLength);
                String info = infoST.render();
                logger.info(info);

                if(dataType.equals("char") || dataType.equals("varchar") || dataType.equals("nvarchar")){
                    tableFileMapping.getPathSet(tableKey).forEach(path -> {
                        //进行DTSX文件中字段长度的替换处理
                        try {
                            byte[] bytes = Files.readAllBytes(path);
                            String sourceContent = new String(bytes);
                            ReplaceFieldLength replaceFieldLength = new ReplaceFieldLength();
                            replaceFieldLength.setContent(sourceContent);
                            replaceFieldLength.setFieldName(odsFieldName);
                            replaceFieldLength.setDataType(dataType);
                            replaceFieldLength.setTargetLength(bizFieldLength);
                            String targetContent = replaceFieldLength.process();
                            Files.write(path, targetContent.getBytes(), StandardOpenOption.WRITE);
                        } catch (IOException e) {
                            ST warnST = new ST("将文件'<path>'中的字段'<odsFieldName>'长度替换为'<bizFieldLength>'时发生异常。");
                            warnST.add("path", path.toString());
                            warnST.add("odsFieldName", odsFieldName);
                            warnST.add("bizFieldLength", bizFieldLength);
                            String warn = warnST.render();
                            logger.warn(warn, e);
                        }
                    });
                }else{
                    ST warnST = new ST("暂时不支持在ODS中对业务系统'<bizName>'的表'<odsTableName>'的'<odsFieldType>'类型的字段'<odsFieldName>'进行长度类型转换，目前仅支持char,varchar,nvarchar类型。");
                    warnST.add("bizName", bizName);
                    warnST.add("odsTableName", odsTableName);
                    warnST.add("odsFieldName", odsFieldName);
                    warnST.add("odsFieldType", dataType);
                    String warn = warnST.render();
                    logger.warn(warn);
                }

                //输出对ODS数据库对应字段进行长度扩充
                TableAlter tableAlter = new TableAlter();
                tableAlter.setOdsRepository(odsRepository);
                tableAlter.setTableName(odsTableName);
                tableAlter.setFieldName(odsFieldName);
                tableAlter.setDataType(dataType);
                tableAlter.setTargetLength(bizFieldLength);
                try {
                    tableAlter.process();
                } catch (Exception e) {
                    ST warnST = new ST("对ODS中对应业务系统'<bizName>'的表'<odsTableName>'的'<odsFieldName>'字段长度由'<odsFieldLength>'扩为'<bizFieldLength>'时发生异常。");
                    warnST.add("bizName", bizName);
                    warnST.add("odsTableName", odsTableName);
                    warnST.add("odsFieldName", odsFieldName);
                    warnST.add("odsFieldLength", odsFieldLength);
                    warnST.add("bizFieldLength", bizFieldLength);
                    String warn = warnST.render();
                    logger.warn(warn, e);
                }
            }
        });
        System.out.println("处理完毕...");
    }

    public void setEtlRepository(EtlRepository etlRepository) {
        this.etlRepository = etlRepository;
    }

    public void setOdsRepository(Repository odsRepository) {
        this.odsRepository = odsRepository;
    }

    public void setBizRepository(Repository bizRepository) {
        this.bizRepository = bizRepository;
    }

    public void setTableFileMapping(TableFileMapping tableFileMapping) {
        this.tableFileMapping = tableFileMapping;
    }
}
