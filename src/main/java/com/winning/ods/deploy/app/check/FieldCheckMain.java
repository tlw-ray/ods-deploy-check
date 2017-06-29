package com.winning.ods.deploy.app.check;

import com.winning.ods.deploy.dao.EtlRepository;
import com.winning.ods.deploy.dao.Repository;
import com.winning.ods.deploy.domain.OrganizationCode;
import com.winning.ods.deploy.app.check.service.ChecksService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
public class FieldCheckMain {

    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        EtlRepository etlRepository = new EtlRepository();
        Repository odsRepository = etlRepository.fetchOdsRepository();
        List<Repository> bizRepositories = etlRepository.fetchBizRepository();
        OrganizationCode organizationCode = etlRepository.fetchOrganizationCodeList().get(0);
        ChecksService checksService = new ChecksService();
        checksService.setTargetRepository(odsRepository);
        checksService.setEtlRepository(etlRepository);
        checksService.setOrganizationCode(organizationCode);
        bizRepositories.forEach(bizRepository -> {
            checksService.setSourceRepository(bizRepository);
            try {
                checksService.process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
