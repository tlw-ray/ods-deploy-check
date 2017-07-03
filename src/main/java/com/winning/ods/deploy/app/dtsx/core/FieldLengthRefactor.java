package com.winning.ods.deploy.app.dtsx.core;

import org.javatuples.Pair;
import org.stringtemplate.v4.ST;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tlw@winning.com.cn on 2017/6/19.
 * 字符串中执行字段长度替换
 */
public class RefactorFieldLength extends AbstractRefactor{

    static private String OUTPUT_COLUMN_PATTERN_TEMPLATE = "<outputColumn id=\"{attributePattern}\" name=\"{fieldName}\" description=\"{attributePattern}\" lineageId=\"{attributePattern}\" precision=\"{attributePattern}\" scale=\"{attributePattern}\" length=\"{attributePattern}\"";
    static private String EXTERNAL_METADATA_COLUMN_PATTERN_TEMPLATE = "<externalMetadataColumn id=\"{attributePattern}\" name=\"{fieldName}\" description=\"{attributePattern}\" precision=\"{attributePattern}\" scale=\"{attributePattern}\" length=\"{attributePattern}\"";
    static private String FIELD_DEFINE_TEMPLATE = "\\[{fieldName}\\] \\[{dataType}\\]\\(\\d+\\)";

    protected String fieldName;
    protected String dataType;
    protected int targetLength;

    //输出
//    Set<Pair<String, String>> replaceSet;   //替换计划

    public String doReplace(String content) {
//        replaceSet = new HashSet();
        String replacedContent = content;
        replacedContent = findOutputColumnReplacement(replacedContent);
        replacedContent = findExternalMetadataColumnReplacement(replacedContent);
        replacedContent = findFieldDefineReplacement(replacedContent);
        return replacedContent;
    }

    private String findOutputColumnReplacement(String content){
        return findXmlReplacement(content, OUTPUT_COLUMN_PATTERN_TEMPLATE);
    }

    private String findExternalMetadataColumnReplacement(String content){
        return findXmlReplacement(content, EXTERNAL_METADATA_COLUMN_PATTERN_TEMPLATE);
    }

    private String findXmlReplacement(String content, String patternTemplate) {
        ST st = new ST(patternTemplate, '{', '}');
        st.add("fieldName", fieldName);
        st.add("attributePattern", "[^\"]*");
        String patternStr = st.render();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        //多处,一般是两处
        String replacedContent = content;
        while(matcher.find()){
            String replaceFrom = matcher.group();
            int lastEqualPosition = replaceFrom.lastIndexOf('=');
//            String oldLength = replaceFrom.substring(lastEqualPosition + 2, replaceFrom.length() - 1);
            String replaceTo = replaceFrom.substring(0, lastEqualPosition + 1) + "\"" + targetLength + "\"";
            log(replaceFrom, replaceTo);
//            replaceSet.add(new Pair(replaceFrom, oldLength));
            replacedContent = replacedContent.replace(replaceFrom, replaceTo);
        }
        return replacedContent;
    }

    private String findFieldDefineReplacement(String content){
        ST st = new ST(FIELD_DEFINE_TEMPLATE, '{', '}');
        st.add("fieldName", fieldName);
        st.add("dataType", dataType);

        String patternStr = st.render();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);

        String replacedContent = content;
        if(matcher.find()){
            String replaceFrom = matcher.group();
            int lastLeftBracketPosition = replaceFrom.lastIndexOf('(');
//            int lastRightBracketPosition = replaceFrom.lastIndexOf(')');
//            String oldLength = replaceFrom.substring(lastLeftBracketPosition + 1, lastRightBracketPosition);
            String replaceTo = replaceFrom.substring(0, lastLeftBracketPosition) + "(" + targetLength + ")";
            log(replaceFrom, replaceTo);
//            replaceSet.add(new Pair(replaceFrom, oldLength));
            replacedContent = replacedContent.replace(replaceFrom, replaceTo);
        }
        return replacedContent;
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
//    public Set<Pair<String, String>> getReplaceSet() {
//        return replaceSet;
//    }
}
