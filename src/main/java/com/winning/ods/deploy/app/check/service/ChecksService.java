package com.winning.ods.deploy.app.check.service;

import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import com.winning.ods.deploy.domain.OrganizationCode;
import org.javatuples.Pair;

import java.util.*;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class ChecksService{

    protected Repository sourceRepository;
    protected EtlRepository etlRepository;
    protected Repository targetRepository;

    protected OrganizationCode organizationCode;

    protected FieldLengthCheckService fieldLengthCheckService = new FieldLengthCheckService();
    protected TimeTempFieldCheckService timeTempFieldCheckService = new TimeTempFieldCheckService();

    public void process() throws Exception {
        //建立执行批次号
        int lsnId = (int)(new Date().getTime() / 1000L);
        String orgCode = organizationCode.getCode();
        String sourceBizName = getSourceRepository().getBizDatabase().getBizName();
        String targetBizName = getTargetRepository().getBizDatabase().getBizName();

        //取数据
        Set<Pair<String, String>> definedTableFieldSet = etlRepository.fetchDefineTableField(sourceBizName);
        Set<String> tableNameSet = new HashSet();
        definedTableFieldSet.forEach(tableField -> tableNameSet.add(tableField.getValue0()));
        Map<Pair<String, String>, Field> sourceFieldMap = sourceRepository.fetchIndexedFieldInfo(tableNameSet);
        Map<Pair<String, String>, Field> targetFieldMap = targetRepository.fetchIndexedFieldInfo(tableNameSet);

        //设定检查所需的相关属性
        fieldLengthCheckService.setOrgCode(orgCode);
        fieldLengthCheckService.setSourceBizName(sourceBizName);
        fieldLengthCheckService.setTargetBizName(targetBizName);
        fieldLengthCheckService.setSourceFieldMap(sourceFieldMap);
        fieldLengthCheckService.setTargetFieldMap(targetFieldMap);
        fieldLengthCheckService.setEtlRepository(etlRepository);
        //进行字段兼容性检查
        fieldLengthCheckService.process();
        //将检查结果输出到控制台
        fieldLengthCheckService.print();
        //将检查结果输出到数据库
        fieldLengthCheckService.writeCheckResult(lsnId);

        //设定检查所需的相关属性
        timeTempFieldCheckService.setOrgCode(organizationCode.getCode());
        timeTempFieldCheckService.setSourceBizName(sourceBizName);
        timeTempFieldCheckService.setTargetBizName(targetBizName);
        timeTempFieldCheckService.setDefinedTimeTempSet(definedTableFieldSet);
        timeTempFieldCheckService.setCheckTimeTempSet(sourceFieldMap.keySet());
        timeTempFieldCheckService.setEtlRepository(getEtlRepository());
        //进行时间戳字段检查
        timeTempFieldCheckService.process();
        //将检查结果输出到控制台
        timeTempFieldCheckService.print();
        //将检查结果输出到数据库
        timeTempFieldCheckService.writeCheckResult(lsnId);
    }

    public Repository getSourceRepository() {
        return sourceRepository;
    }

    public void setSourceRepository(Repository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public EtlRepository getEtlRepository() {
        return etlRepository;
    }

    public void setEtlRepository(EtlRepository etlRepository) {
        this.etlRepository = etlRepository;
    }

    public Repository getTargetRepository() {
        return targetRepository;
    }

    public void setTargetRepository(Repository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public void setOrganizationCode(OrganizationCode organizationCode) {
        this.organizationCode = organizationCode;
    }

}
