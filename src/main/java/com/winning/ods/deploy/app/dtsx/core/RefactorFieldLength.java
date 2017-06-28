package com.winning.ods.deploy.app.dtsx.core;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/6/19.
 * 字符串中执行字段长度替换
 */
public class ReplaceFieldLength {

    static private String OUTPUT_COLUMN_PATTERN_TEMPLATE = "<outputColumn id=\"{attributePattern}\" name=\"{fieldName}\" description=\"{attributePattern}\" lineageId=\"{attributePattern}\" precision=\"{attributePattern}\" scale=\"{attributePattern}\" length=\"{attributePattern}\"";
    static private String EXTERNAL_METADATA_COLUMN_PATTERN_TEMPLATE = "<externalMetadataColumn id=\"{attributePattern}\" name=\"{fieldName}\" description=\"{attributePattern}\" precision=\"{attributePattern}\" scale=\"{attributePattern}\" length=\"{attributePattern}\"";
    static private String FIELD_DEFINE_TEMPLATE = "\\[{fieldName}\\] \\[{dataType}\\]\\(\\d+\\)";

    private Logger logger = LoggerFactory.getLogger(getClass());

    //输入
    protected String content;

    protected String fieldName;
    protected String dataType;
    protected int targetLength;

    Map<String ,String> replaceTaskMap;

    //输出
    Set<Pair<String, String>> replaceSet;   //替换了哪些内容
    Set<String> lengthSet;                  //替换的原长度集合

    public String process(){
        replaceTaskMap = new HashMap();
        replaceSet = new HashSet();
        lengthSet = new HashSet();
        findOutputColumnReplacement();
        findExternalMetadataColumnReplacement();
        findFieldDefineReplacement();

        String targetContent = content;

        for(Map.Entry<String, String> replaceEntry : replaceTaskMap.entrySet()){
            targetContent = targetContent.replace(replaceEntry.getKey(), replaceEntry.getValue());
        }

        return targetContent;
    }

    private void findOutputColumnReplacement(){
        findXmlReplacement(OUTPUT_COLUMN_PATTERN_TEMPLATE);
    }

    private void findExternalMetadataColumnReplacement(){
        findXmlReplacement(EXTERNAL_METADATA_COLUMN_PATTERN_TEMPLATE);
    }

    private void findXmlReplacement(String patternTemplate) {
        ST st = new ST(patternTemplate, '{', '}');
        st.add("fieldName", fieldName);
        st.add("attributePattern", "[^\"]*");
        String patternStr = st.render();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        //多处,一般是两处
        while(matcher.find()){
            String replaceFrom = matcher.group();
            int lastEqualPosition = replaceFrom.lastIndexOf('=');
            String oldLength = replaceFrom.substring(lastEqualPosition + 2, replaceFrom.length() - 1);
            String replaceTo = replaceFrom.substring(0, lastEqualPosition + 1) + "\"" + targetLength + "\"";
            log(replaceFrom, replaceTo);
            replaceSet.add(new Pair(replaceFrom, oldLength));
            replaceTaskMap.put(replaceFrom, replaceTo);
            lengthSet.add(oldLength);
        }
    }

    private void findFieldDefineReplacement(){
        ST st = new ST(FIELD_DEFINE_TEMPLATE, '{', '}');
        st.add("fieldName", fieldName);
        st.add("dataType", dataType);

        String patternStr = st.render();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
            String replaceFrom = matcher.group();
            int lastLeftBracketPosition = replaceFrom.lastIndexOf('(');
            int lastRightBracketPosition = replaceFrom.lastIndexOf(')');
            String oldLength = replaceFrom.substring(lastLeftBracketPosition + 1, lastRightBracketPosition);
            String replaceTo = replaceFrom.substring(0, lastLeftBracketPosition) + "(" + targetLength + ")";
            log(replaceFrom, replaceTo);
            replaceSet.add(new Pair(replaceFrom, oldLength));
            replaceTaskMap.put(replaceFrom, replaceTo);
            lengthSet.add(oldLength);
        }
    }

    private void log(String replaceFrom, String replaceTo) {
        logger.debug("替换: " + replaceFrom + "\t 为: " + replaceTo);
    }

    public void setContent(String content) {
        this.content = content;
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

    public Set<Pair<String, String>> getReplaceSet() {
        return replaceSet;
    }

    public Set<String> getLengthSet() {
        return lengthSet;
    }
}
