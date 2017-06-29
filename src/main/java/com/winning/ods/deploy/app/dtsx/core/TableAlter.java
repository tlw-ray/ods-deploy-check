package com.winning.ods.deploy.app.dtsx.core;

import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.Database;
import com.winning.ods.deploy.util.SqlServer;
import com.winning.ods.deploy.util.SqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.sql.SQLException;

/**
 * Created by tlw@winning.com.cn on 2017/6/20.
 * alter table 语句声称，可执行检查和执行
 */
public class TableAlter {


    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Repository odsRepository;
    protected String tableName;
    protected String fieldName;
    protected String dataType;
    protected int targetLength;

    public void process() throws SQLException, ClassNotFoundException {
//        //仅输出改表语句
        String alterTableQuery = SqlUtil.generateAlterTableQuery(tableName, fieldName, dataType, targetLength);
//        System.out.println(alterTableQuery);

        //检查表中是否已经有数据
        if(odsRepository.hasRow(tableName)){
            ST warnMessageST = new ST("DOS表'<tableName>'已经包含数据，请根据情况手动执行语句<alterTable>来修改字段<fieldName>的长度为<targetLength>。");
            warnMessageST.add("tableName", tableName);
            warnMessageST.add("alterTable", alterTableQuery);
            warnMessageST.add("fieldName", fieldName);
            warnMessageST.add("targetLength", targetLength);
            String warnMessage = warnMessageST.render();
            logger.warn(warnMessage);
        }else{
            odsRepository.executeQuery(alterTableQuery);
            logger.info(alterTableQuery);
        }
    }

    public void setOdsRepository(Repository odsRepository) {
        this.odsRepository = odsRepository;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setTargetLength(int targetLength) {
        this.targetLength = targetLength;
    }
}
