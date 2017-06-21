package com.winning.ods.deploy.app.dtsx.tool;

import com.winning.ods.deploy.app.check.core.FieldChecker;
import com.winning.ods.deploy.app.dtsx.core.ReplaceFieldLength;
import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import com.winning.ods.deploy.app.dtsx.service.RefactorFieldLengthService;
import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.LogManager;

/**
 * Created by tlw@winning.com.cn on 2017/6/20.
 * 仅作检查不做修改
 */
public class CheckFieldLengthMain {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        File logFile = new File("config/log.properties");
        FileInputStream fileInputStream = new FileInputStream(logFile);
        LogManager.getLogManager().readConfiguration(fileInputStream);

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        EtlRepository etlRepository = new EtlRepository();
        Repository odsRepository = etlRepository.fetchOdsRepository();
        List<Repository> bizRepositories = etlRepository.fetchBizRepository();

        PrintWriter diffLog = new PrintWriter(System.out);

        TableFileMapping tableFileMapping = new TableFileMapping();
        tableFileMapping.init();

        //遍历业务系统
        bizRepositories.forEach(bizRepository -> {
            try {
                //取字段长度信息
                String bizName = bizRepository.getBizDatabase().getBizName();
                String odsName = odsRepository.getBizDatabase().getBizName();
                Set<Pair<String, String>> definedTableFieldSet = etlRepository.fetchDefineTableField(bizName);
                Set<String> tableNameSet = new HashSet();
                definedTableFieldSet.forEach(tableField -> tableNameSet.add(tableField.getValue0()));
                Map<Pair<String, String>, Field> bizFieldMap = bizRepository.fetchIndexedFieldInfo(tableNameSet);
                Map<Pair<String, String>, Field> odsFieldMap = odsRepository.fetchIndexedFieldInfo(tableNameSet);

                FieldChecker fieldChecker = new FieldChecker();
                fieldChecker.setSourceFieldMap(bizFieldMap);
                fieldChecker.setTargetFieldMap(odsFieldMap);
                fieldChecker.process();
                TreeSet<Pair<String, String>> lengthConflictFieldSet = fieldChecker.getLengthConflictFieldSet();

                lengthConflictFieldSet.forEach( tableField -> {
//                bizFieldMap.keySet().forEach( tableField -> {
                    Field bizField = bizFieldMap.get(tableField);
                    String bizTableName = bizField.getTableName();
                    String bizFieldName = bizField.getFieldName();
                    String bizDataType = bizField.getDataType();
                    int bizFieldLength = bizField.getCharacterMaximumLength();

                    if(bizDataType.equals("char") || bizDataType.equals("varchar") || bizDataType.equals("nvarchar")) {
                        String fileName = bizTableName.trim().toUpperCase();
                        Set<Path> pathSet = tableFileMapping.getPathSet(fileName);
                        if (pathSet != null) {
                            pathSet.forEach(path -> {
                                try {
                                    byte[] bytes = Files.readAllBytes(path);
                                    String content = new String(bytes);
                                    ReplaceFieldLength replaceFieldLength = new ReplaceFieldLength();
                                    replaceFieldLength.setDataType(bizDataType);
                                    replaceFieldLength.setFieldName(bizFieldName);
                                    replaceFieldLength.setTargetLength(bizFieldLength);
                                    replaceFieldLength.setContent(content);
                                    replaceFieldLength.process();
                                    if(replaceFieldLength.getLengthSet().size() > 1) {
                                        Field odsField = odsFieldMap.get(tableField);
                                        String odsTableName = odsField.getTableName();
                                        String odsFieldName = odsField.getFieldName();
                                        String odsDataType = odsField.getDataType();
                                        int odsFieldLength = odsField.getCharacterMaximumLength();
                                        diffLog.println(bizName + "." + bizTableName + "." + bizFieldName + " " + bizDataType + "(" + bizFieldLength + ")");
                                        diffLog.println("\t" + odsName + "." + odsTableName + "." + odsFieldName + " " + odsDataType + "(" + odsFieldLength + ")");
                                        diffLog.println("\t\t" + path.toString());
                                        replaceFieldLength.getReplaceSet().forEach(pair -> diffLog.println("\t\t\t" + pair.getValue0()));
                                        diffLog.println();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });
            }catch(SQLException e){
                //日志处理
                e.printStackTrace();
            }
        });
        diffLog.flush();
        diffLog.close();
    }

}
