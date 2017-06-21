package com.winning.ods.deploy.app.check.service;

import com.winning.ods.deploy.app.check.core.FieldChecker;
import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.domain.Field;
import com.winning.ods.deploy.domain.FieldCheckResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 * 检查ETL过程中，数据源(业务系统)到目标(ODS系统)字段兼容性
 * 1. 检查源到目标，源中可能缺失的字段
 * 2. 检查源到目标，源中表名、字段名符合映射规则，但字段类型有冲突的字段
 * 3. 检查源到目标，源中表名、字段名、类型都一致，但长度有冲突的字段
 */
public class FieldLengthCheckService{

    public static final String FIELD_MISSING = "COLUMN_MISSING";                    //字段缺失
    public static final String FIELD_TYPE_CONFLICT = "COLUMN_TYPE_CONFLICT";        //字段类型冲突
    public static final String FIELD_LENGTH_CONFLICT = "COLUMN_LENGTH_CONFLICT";    //字段长度冲突

    protected FieldChecker fieldChecker = new FieldChecker();

    //输入
    protected String orgCode;
    protected String sourceBizName;
    protected String targetBizName;
    protected EtlRepository etlRepository;

    //日志输出
    protected PrintWriter printWriter = new PrintWriter(System.out);

    /**
     * 执行检查
     * @throws SQLException
     */
    public void process() throws SQLException {
        fieldChecker.process();
    }

    /**
     * 将所有的检查结果集写入数据库
     * @param lsnId 批次号
     * @throws SQLException 数据库操作时可能发生的异常
     */
    public void writeCheckResult(int lsnId) throws SQLException {
        writeFieldCheckResultList(lsnId, FIELD_MISSING, fieldChecker.getMissingFieldSet());
        writeFieldCheckResultList(lsnId, FIELD_TYPE_CONFLICT, fieldChecker.getTypeConflictFieldSet());
        writeFieldCheckResultList(lsnId, FIELD_LENGTH_CONFLICT, fieldChecker.getLengthConflictFieldSet());
    }

    /**
     * 将一个检查结果集写入数据库
     * @param lsnId 批次号
     * @param informationCode 检查信息码
     * @param tableFieldSet 检查出的表字段集合
     * @throws SQLException 数据库操作时可能发生的异常
     */
    private void writeFieldCheckResultList(int lsnId, String informationCode, Set<Pair<String, String>> tableFieldSet) throws SQLException {
        List<FieldCheckResult> fieldCheckResultList = new ArrayList();
        tableFieldSet.forEach(tableField -> {
            Field targetField = fieldChecker.getTargetFieldMap().get(tableField);
            Field sourceField = fieldChecker.getSourceFieldMap().get(tableField);
            FieldCheckResult fieldCheckResult = new FieldCheckResult();
            fieldCheckResult.setOrgCode(orgCode);
            fieldCheckResult.setTargetName(targetBizName);
            fieldCheckResult.setTargetTableName(targetField.getTableName());
            fieldCheckResult.setTargetFieldName(targetField.getFieldName());
            fieldCheckResult.setTargetFieldType(targetField.getDataType());
            fieldCheckResult.setTargetFieldLength(targetField.getCharacterMaximumLength());
            fieldCheckResult.setSourceName(sourceBizName);
            if(sourceField != null){
                fieldCheckResult.setSourceFieldLength(sourceField.getCharacterMaximumLength());
                fieldCheckResult.setSourceFieldType(sourceField.getDataType());
            }
            fieldCheckResult.setLsnId(lsnId);
            fieldCheckResult.setInformationCode(informationCode);
            fieldCheckResultList.add(fieldCheckResult);
        });
        etlRepository.insertFieldCompareResult(fieldCheckResultList);
    }

    /**
     * 将检查结果输出到System.out
     * @throws IOException IO操作时可能发生的异常
     */
    public void print() throws IOException {
        printWriter.write(sourceBizName);
        printWriter.write("缺失字段明细:  \n");
        printContent(fieldChecker.getMissingFieldSet());
        printWriter.write("\n");
        printWriter.write(sourceBizName);
        printWriter.write("类型不兼容字段明细: \n");
        printContent(fieldChecker.getTypeConflictFieldSet());
        printWriter.write("\n");
        printWriter.write(sourceBizName);
        printWriter.write("长度不兼容字段明细：  \n");
        printContent(fieldChecker.getLengthConflictFieldSet());
        printWriter.write("\n");
        printWriter.flush();
    }

    /**
     * 将检查结果的内容输出
     * @param fieldSet  表字段集合
     * @throws IOException IO操作时可能发生的异常
     */
    protected void printContent(Set<Pair<String, String>> fieldSet) throws IOException {
        Map<Pair<String, String>, Field> checkFieldMap = fieldChecker.getSourceFieldMap();
        Map<Pair<String, String>, Field> definedFieldMap = fieldChecker.getTargetFieldMap();
        int num = 1;
        CSVPrinter csvPrinter = new CSVPrinter(printWriter, CSVFormat.DEFAULT);
        csvPrinter.printRecord("#序号", "表名", "字段名", targetBizName + "字段类型", targetBizName + "字段长度", sourceBizName + "字段类型", sourceBizName + "字段长度");
        for(Pair<String, String> tableField : fieldSet) {
            Field definedField = definedFieldMap.get(tableField);
            Field checkField = checkFieldMap.get(tableField);
            String tableName = definedField.getTableName();
            String fieldName = definedField.getFieldName();

            String definedFieldType = "";
            String definedFieldLength = "";

            if(definedField != null){
                definedFieldType = definedField.getDataType();
                Integer length = definedField.getCharacterMaximumLength();
                if(length != null){
                    definedFieldLength = Integer.toString(length);
                }
            }

            String checkFieldType = "";
            String checkFieldLength = "";

            //如果业务系统没有缺少该字段则为该字段赋值,否则显示空
            if(checkField != null) {
                //空判断，避免出现空指针异常
                checkFieldType = checkField.getDataType();
                Integer length = checkField.getCharacterMaximumLength();
                if(length != null) {
                    checkFieldLength = Integer.toString(length);
                }
            }
            csvPrinter.printRecord(num++, tableName, fieldName, definedFieldType, definedFieldLength, checkFieldType, checkFieldLength);
        }
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setSourceBizName(String sourceBizName) {
        this.sourceBizName = sourceBizName;
    }

    public void setTargetBizName(String targetBizName) {
        this.targetBizName = targetBizName;
    }

    public void setEtlRepository(EtlRepository etlRepository) {
        this.etlRepository = etlRepository;
    }

    public void setSourceFieldMap(Map<Pair<String, String>, Field> sourceFieldMap){
        fieldChecker.setSourceFieldMap(sourceFieldMap);
    }

    public void setTargetFieldMap(Map<Pair<String, String>, Field> targetFieldMap){
        fieldChecker.setTargetFieldMap(targetFieldMap);
    }
}
