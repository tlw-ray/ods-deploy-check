package com.winning.ods.deploy.app.dtsx.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/19.
 * 建立从表名到路径的映射关系
 */
public class TableFileMapping {

    Logger logger = LoggerFactory.getLogger(getClass());

    String home = "";

    Map<String, Set<Path>> tablePathMap;

    public void init() {
        tablePathMap = new HashMap();
        Path rootPath = Paths.get(home);
        traversal(rootPath);
    }

    public void traversal(Path rootPath){
        if(Files.isDirectory(rootPath)){
            try {
                Files.list(rootPath).forEach(path -> traversal(path));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }else{
            String fileName = rootPath.getFileName().toString();
            if(fileName.toLowerCase().endsWith(".dtsx")){
                String tableName = fileName.substring(0, fileName.length() - 5);
                Set<Path> pathSet = tablePathMap.get(tableName);
                if(pathSet == null){
                    pathSet = new HashSet();
                    tablePathMap.put(tableName, pathSet);
                }
                pathSet.add(rootPath);
            }
        }
    }

    public void setHome(String home) {
        this.home = home;
    }

    public Set<Path> getPathSet(String tableName){
        return tablePathMap.get(tableName);
    }
}
