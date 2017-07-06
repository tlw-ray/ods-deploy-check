package com.winning.ods.deploy.app.dtsx.core;

import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/19.
 */
public class TestTableFileMapping {
    @Test
    public void testTableFileMapping() throws IOException {
        TableFileMapping mapping = new TableFileMapping();
        mapping.init();
        Set<Path> pathSet = mapping.getPathSet("ZY_BRSYK");
        pathSet.forEach(path->System.out.println(path));
    }
}
