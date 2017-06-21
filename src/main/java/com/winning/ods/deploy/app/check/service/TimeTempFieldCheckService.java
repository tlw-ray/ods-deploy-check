package com.winning.ods.deploy.app.check.service;

import com.winning.ods.deploy.app.check.core.TimestampFieldCheck;
import com.winning.ods.deploy.dao.EtlRepository;
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
 * 检查业务系统时间戳字段
 */
public class TimeTempFieldCheckService{

    public static final String TABLE_MISSING_TIME_TEMP = "TABLE_MISSING_TIMETEMP";

    protected String orgCode;
    protected String sourceBizName;
    protected String targetBizName;
    protected EtlRepository etlRepository;

    protected TimestampFieldCheck timeTempFieldCheck = new TimestampFieldCheck();

    protected PrintWriter printWriter = new PrintWriter(System.out);

    public void process() throws SQLException {
        timeTempFieldCheck.process();
    }

    public void writeCheckResult(int lsnId) throws SQLException {
        Set<Pair<String, String>> tableFieldSet = timeTempFieldCheck.getLackTimeTemp();
        List<FieldCheckResult> fieldCheckResultList = new ArrayList();
        tableFieldSet.forEach(tableField -> {
            FieldCheckResult fieldCheckResult = new FieldCheckResult();
            fieldCheckResult.setOrgCode(orgCode);
            fieldCheckResult.setTargetName(targetBizName);
            fieldCheckResult.setTargetTableName(tableField.getValue0());
            fieldCheckResult.setTargetFieldName(tableField.getValue1());
            fieldCheckResult.setSourceName(sourceBizName);
            fieldCheckResult.setLsnId(lsnId);
            fieldCheckResult.setInformationCode(TABLE_MISSING_TIME_TEMP);
            fieldCheckResultList.add(fieldCheckResult);
        });
        etlRepository.insertFieldCompareResult(fieldCheckResultList);
    }

    public void print() throws IOException {
        Set<Pair<String, String>> lackTimeTemp = timeTempFieldCheck.getLackTimeTemp();
        printWriter.write(sourceBizName);
        printWriter.write("系统TimeTemp字段缺失: \n");

        CSVPrinter printer = new CSVPrinter(printWriter, CSVFormat.DEFAULT);
        printer.printRecord("序号", "表名", "时间戳字段");
        int num = 1;
        for(Pair<String, String> tableField : lackTimeTemp){
            printer.printRecord(num++, tableField.getValue0(), tableField.getValue1());
        }
        printWriter.flush();
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

    public void setDefinedTimeTempSet(Set<Pair<String, String>> definedTimeTempSet){
        timeTempFieldCheck.setDefinedTimestampSet(definedTimeTempSet);
    }

    public void setCheckTimeTempSet(Set<Pair<String, String>> sourceTimeTempSet){
        timeTempFieldCheck.setSourceTimestampSet(sourceTimeTempSet);
    }
}
