package com.winning.ods.deploy.app.dtsx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by tlw@winning.com.cn on 2017/6/23.
 */
public abstract class FileRefactor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    //输入
    private Path sourcePath;
    private Path targetPath;

    public void process(){
        try {
            byte[] bytes = Files.readAllBytes(sourcePath);
            String content = new String(bytes, "UTF-8");
            String targetContent = doReplace(content);
            if(targetPath.toFile().exists()) {
                Files.delete(targetPath); //避免改写后的长度大于以有长度，先删除再写入
            }
            Files.write(targetPath, targetContent.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW);
        }catch(IOException e){
            logger.warn("转换文件{}时异常: ", e.getMessage());
        }
    }

    abstract String doReplace(String content);

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    protected void log(String replaceFrom, String replaceTo) {
        logger.debug("替换: " + replaceFrom + "\t 为: " + replaceTo);
    }

}
