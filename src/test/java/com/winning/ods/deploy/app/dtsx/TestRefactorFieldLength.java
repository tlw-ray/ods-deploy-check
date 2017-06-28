package com.winning.ods.deploy.app.dtsx;

import com.winning.ods.deploy.app.dtsx.core.RefactorFieldLength;
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
public class TestReplaceFieldLength {
    @Test
    public void testReplaceFieldLength() throws IOException {
        Path sourcePath = Paths.get("test-data/ZY_BRSYK.dtsx");
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceContent = new String(sourceBytes);

        Path targetPath = Paths.get("test-data/ZY_BRSYK.dtsx.target");
        byte[] targetBytes = Files.readAllBytes(targetPath);
        String targetContent = new String(targetBytes);

        RefactorFieldLength refactorFieldLength = new RefactorFieldLength();
        refactorFieldLength.setFieldName("pzh2");
        refactorFieldLength.setDataType("varchar");
        refactorFieldLength.setTargetLength(128);
        refactorFieldLength.setContent(sourceContent);
        String replacedContent = refactorFieldLength.process();

        Assert.assertEquals(targetContent, replacedContent);
    }
}
