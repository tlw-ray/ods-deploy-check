package com.winning.ods.deploy.app.check.core;

import com.winning.ods.deploy.domain.Field;
import org.javatuples.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by tlw@winning.com.cn on 2017/6/11.
 *
 * 将某数据库导入，结构被定义的库时需要检查字段是否兼容：
 * 1. 字段未定义错误: 该数据库使用了未定义的字段。
 * 2. 字段类型冲突错误: 该数据库使用了与定义类型冲突的字段。
 * 3. 字段长度不兼容错误： 该数据库使用了类型兼容，但长度无法导入的字段。
 * 4. 字段缺失警告：已定义的字段在该数据库中不存在。
 */
public class FieldChecker {

    //目标库特有字段列表
    static Set<String> excludeField = new HashSet();
    static {
        excludeField.add("createtime");
        excludeField.add("gxrq");
        excludeField.add("gxrqtimestr");
        excludeField.add("timetempstr");
        excludeField.add("isnew");
        excludeField.add("isdelete");
        excludeField.add("lsnid");
        excludeField.add("sys_id");
        excludeField.add("yljgdm");
        excludeField.add("timetemp");
        excludeField.add("timetempstr_cdc");
        excludeField.add("iscurrent");
    }

    //输入
    protected Map<Pair<String, String>, Field> targetFieldMap;           //目标库字段(ODS)
    protected Map<Pair<String, String>, Field> sourceFieldMap;           //源库字段(业务系统)

    //输出
    protected TreeSet<Pair<String, String>> undefinedFieldSet;              //未定义: 使用未定义的字段
    protected TreeSet<Pair<String, String>> typeConflictFieldSet;           //类型冲突: 存在但类型冲突的字段
    protected TreeSet<Pair<String, String>> lengthConflictFieldSet;         //长度不兼容: 存在且类型能够兼容，但长度不兼容的字段
    protected TreeSet<Pair<String, String>> missingFieldSet;                //缺少: 定义但未使用的字段

    public void process() {
        undefinedFieldSet = new TreeSet();
        typeConflictFieldSet = new TreeSet();
        lengthConflictFieldSet = new TreeSet();
        sourceFieldMap.keySet().forEach(tableField -> {
            Field checkField = sourceFieldMap.get(tableField);
            Field defineField = targetFieldMap.get(tableField);

            //注意： 检查的时候，依据表名转大写，字段名转小写来判定的规则
            String checkFieldName = tableField.getValue1().trim().toLowerCase();

            if(excludeField.contains(checkFieldName) || checkFieldName.endsWith("key")){
                //对特定字段不做检查
            }else{
                //检查未定义的字段
                if (defineField == null) {
                    //如果该字段未定义则认为使用了未定义的字段
                    undefinedFieldSet.add(tableField);
                } else {
                    String defineFieldType = defineField.getDataType();
                    Integer defineFieldLength = defineField.getCharacterMaximumLength();
                    String checkFieldType = checkField.getDataType();
                    Integer checkFieldLength = checkField.getCharacterMaximumLength();
                    //判断类型是否兼容
                    if (typeConflict(defineFieldType, defineFieldLength, checkFieldType, checkFieldLength)) {
                        if(defineFieldType.equals(checkFieldType)){
                            //类型相同则判断长度是否冲突。当长度不为空，并且业务系统中长度大于ODS中长度时记录长度冲突
                            if (defineFieldLength != null && checkFieldLength != null && lengthConflict(defineFieldLength, checkFieldLength)) {
                                lengthConflictFieldSet.add(tableField);
                                //如果一个为空另一个不为空也记录入长度冲突
                            }else if(defineFieldLength == null && checkFieldLength != null){
                                lengthConflictFieldSet.add(tableField);
                            }else if(defineFieldLength != null && checkFieldLength == null){
                                lengthConflictFieldSet.add(tableField);
                            }
                        }
                    } else {
                        //类型不兼容则记录入类型冲突
                        typeConflictFieldSet.add(tableField);
                    }
                }
            }
        });

        //检查缺少的字段
        missingFieldSet = new TreeSet();
        targetFieldMap.keySet().forEach(tableField -> {
            String fieldName = tableField.getValue1();
            if(excludeField.contains(fieldName) || fieldName.endsWith("key")){
                //该字段属于特殊字段，不再这里做缺失检查
            }else{
                //如果该字段已定义，但在检查的字段中无法找到则认为缺失
                if(sourceFieldMap.get(tableField) == null){
                    missingFieldSet.add(tableField);
                }
            }
        });
    }

    protected boolean typeConflict(String definedType, Integer definedLength, String checkType, Integer checkLength){
        //类型完全相同时认为类型兼容
        //此外下面几种情况也是兼容的
        //业务系统  ODS
//        char(16)	datetime
//        char(8)	datetime
//        char(x)   varchar(y) x<=y
        return  definedType.equals(checkType)
                || (definedType.equals("datetime") && checkType.equals("char"))
                || (definedType.equals("datetime") && checkType.equals("varchar"))
                || (definedType.equals("datetime") && checkType.equals("nvarchar"))
                || (definedType.equals("varchar") && checkType.equals("char") && definedLength >= checkLength)
                || (definedType.equals("nvarchar") && checkType.equals("varchar") && definedLength >= checkLength)
                || (definedType.equals("nvarchar") && checkType.equals("char") && definedLength >= checkLength)
                || (definedType.equals("numeric") && checkType.equals("smallint"));
    }

    protected boolean lengthConflict(int definedFieldLength, int checkFieldLength){
        //定义的字段长度大于被检查的字段长度时认为可以兼容
        return definedFieldLength < checkFieldLength;
    }

    public Map<Pair<String, String>, Field> getTargetFieldMap() {
        return targetFieldMap;
    }

    public void setTargetFieldMap(Map<Pair<String, String>, Field> targetFieldMap) {
        this.targetFieldMap = targetFieldMap;
    }

    public Map<Pair<String, String>, Field> getSourceFieldMap() {
        return sourceFieldMap;
    }

    public void setSourceFieldMap(Map<Pair<String, String>, Field> sourceFieldMap) {
        this.sourceFieldMap = sourceFieldMap;
    }

    public TreeSet<Pair<String, String>> getUndefinedFieldSet() {
        return undefinedFieldSet;
    }

    public TreeSet<Pair<String, String>> getTypeConflictFieldSet() {
        return typeConflictFieldSet;
    }

    public TreeSet<Pair<String, String>> getLengthConflictFieldSet() {
        return lengthConflictFieldSet;
    }

    public TreeSet<Pair<String, String>> getMissingFieldSet() {
        return missingFieldSet;
    }

}
