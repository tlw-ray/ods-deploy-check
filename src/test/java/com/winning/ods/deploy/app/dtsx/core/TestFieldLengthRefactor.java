package com.winning.ods.deploy.app.dtsx;

import com.winning.ods.deploy.app.dtsx.core.FieldLengthRefactor;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by tlw@winning.com.cn on 2017/6/19.
 * <outputColumn id="\d+" name="pzh2" description=".*" lineageId="4382" precision=".*" scale=".*" length="32"
 */
public class TestFieldLengthRefactor {

    private void test(Path sourcePath, Path actualPath, Path expectedPath, String fieldName, String dataType, int targetLength) throws IOException {
        FieldLengthRefactor fieldLengthRefactor = new FieldLengthRefactor();
        fieldLengthRefactor.setSourcePath(sourcePath);
        fieldLengthRefactor.setTargetPath(actualPath);
        fieldLengthRefactor.setFieldName(fieldName);
        fieldLengthRefactor.setDataType(dataType);
        fieldLengthRefactor.setTargetLength(targetLength);
        fieldLengthRefactor.process();

        byte[] actualBytes = Files.readAllBytes(actualPath);
        String actualString = new String(actualBytes, "UTF-8");
        byte[] expectBytes = Files.readAllBytes(expectedPath);
        String expectedString = new String(expectBytes, "UTF-8");

        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void test_ZY_BRSYK() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorFieldLength/ZY_BRSYK.dtsx");
        Path actualPath = Paths.get("test-data/refactorFieldLength/ZY_BRSYK.dtsx.actual");
        Path expectedPath = Paths.get("test-data/refactorFieldLength/ZY_BRSYK.dtsx.expected");

        String fieldName = "pzh2";
        String dataType = "varchar";
        int targetLength = 128;

        test(sourcePath, actualPath, expectedPath, fieldName, dataType, targetLength);
    }

    @Test
    public void test_XK_TESTINFO() throws IOException {
        Path sourcePath = Paths.get("test-data/refactorFieldLength/XK_TESTINFO.dtsx");
        Path actualPath = Paths.get("test-data/refactorFieldLength/XK_TESTINFO.dtsx.actual");
        Path expectedPath = Paths.get("test-data/refactorFieldLength/XK_TESTINFO.dtsx.expected");

        String fieldName = "cella";
        String dataType = "varchar";
        int targetLength = 2;

        test(sourcePath, actualPath, expectedPath, fieldName, dataType, targetLength);
    }
}
