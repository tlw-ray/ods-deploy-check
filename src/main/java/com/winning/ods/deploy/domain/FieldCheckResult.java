package com.winning.ods.deploy.domain;

/**
 * Created by tlw@winning.com.cn on 2017/6/14.
 **/
public class FieldCheckResult {

    String orgCode;                         /* 唯一标识一条记录之一，所有业务系统必须统一 */
    String sourceName;                      /* 标识数据来源 */
    String targetName;                      /* 标识数据目标 */
    String targetTableName;                 /* 对比的数据库表名 */
    String targetFieldName;                 /* 列名称 */
    String targetFieldType;                 /* 仓库数据类型 */
    Integer targetFieldLength;              /* 仓库长度 */
    Integer targetFieldPrecision;           /* 如果仓库列包含的是数值，则为该列的精度；否则为 0 */
    String sourceFieldType;                 /* 源数据类型 */
    Integer sourceFieldLength;              /* 源长度 */
    Integer sourceFieldPrecision;           /* 源如果列包含的是数值，则为该列的精度；否则为 0 */
    Integer lsnId;                          /* 批次号 */
    String informationCode;                 /* 对比定义码 */

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }

    public String getTargetFieldType() {
        return targetFieldType;
    }

    public void setTargetFieldType(String targetFieldType) {
        this.targetFieldType = targetFieldType;
    }

    public Integer getTargetFieldLength() {
        return targetFieldLength;
    }

    public void setTargetFieldLength(Integer targetFieldLength) {
        this.targetFieldLength = targetFieldLength;
    }

    public Integer getTargetFieldPrecision() {
        return targetFieldPrecision;
    }

    public void setTargetFieldPrecision(Integer targetFieldPrecision) {
        this.targetFieldPrecision = targetFieldPrecision;
    }

    public String getSourceFieldType() {
        return sourceFieldType;
    }

    public void setSourceFieldType(String sourceFieldType) {
        this.sourceFieldType = sourceFieldType;
    }

    public Integer getSourceFieldLength() {
        return sourceFieldLength;
    }

    public void setSourceFieldLength(Integer sourceFieldLength) {
        this.sourceFieldLength = sourceFieldLength;
    }

    public Integer getSourceFieldPrecision() {
        return sourceFieldPrecision;
    }

    public void setSourceFieldPrecision(Integer sourceFieldPrecision) {
        this.sourceFieldPrecision = sourceFieldPrecision;
    }

    public String getInformationCode() {
        return informationCode;
    }

    public void setInformationCode(String informationCode) {
        this.informationCode = informationCode;
    }

    public Integer getLsnId() {
        return lsnId;
    }

    public void setLsnId(Integer lsnId) {
        this.lsnId = lsnId;
    }
}
