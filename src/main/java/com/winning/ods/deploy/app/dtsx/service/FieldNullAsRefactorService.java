package com.winning.ods.deploy.app.dtsx.service;

import com.winning.ods.deploy.app.check.core.FieldChecker;
import com.winning.ods.deploy.app.dtsx.core.FieldLengthAlter;
import com.winning.ods.deploy.app.dtsx.core.FieldLengthRefactor;
import com.winning.ods.deploy.app.dtsx.core.FieldNullAsRefactor;
import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/7/3.
 */
public class FieldNullAsRefactorService {

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

        //进行字段差异检查
        FieldChecker fieldChecker = new FieldChecker();
        fieldChecker.setSourceFieldMap(sourceFieldMap);
        fieldChecker.setTargetFieldMap(targetFieldMap);
        fieldChecker.process();

        fieldChecker.getMissingFieldSet().forEach(tableField -> {
            //业务系统中如果缺少ODS定义的字段，在DTSX包中的Select语句对该字段的选择应写为null as fieldName
            String missTable = tableField.getValue0();
            String missField = tableField.getValue1();
            String tableKey = missTable.trim().toUpperCase();//表名用来对应*.dtsx文件名，采用大写是约定
            Set<Path> paths = tableFileMapping.getPathSet(tableKey);
            if(paths != null && paths.size() > 0){
                paths.forEach(path -> {
                    logger.info("替换文件{}中字段{}为'Null as {}'", path.toString(), missField, missField);
                    Set<String> missFieldNameSet = new HashSet();
                    missFieldNameSet.add(missField);

                    FieldNullAsRefactor fieldNullAsRefactor = new FieldNullAsRefactor();
                    fieldNullAsRefactor.setSourcePath(path);
                    fieldNullAsRefactor.setTargetPath(path);
                    fieldNullAsRefactor.setTableName(missTable);
                    fieldNullAsRefactor.setFieldNameSet(missFieldNameSet);
                    fieldNullAsRefactor.process();
                });
            }
        });

        logger.info("处理完毕...");
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
