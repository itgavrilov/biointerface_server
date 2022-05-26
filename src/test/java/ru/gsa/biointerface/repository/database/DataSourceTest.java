package ru.gsa.biointerface.repository.database;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.domain.Patient;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

@RunWith(SpringRunner.class)
@SpringBootTest
class DataSourceTest {
    @Autowired
    PatientService service;
    @Autowired
    IcdService icdService;

    @Test
    void getSessionFactory() {
        try {
            Icd icd = new Icd("testName", 10, "testComment");
            icd = icdService.save(icd);

            System.out.println("!!!!!!!!!!" + icd);
            icdService.delete(icd.getId());
            Patient entity = service.getById(3);

            System.out.println("!!!!!!!!!!" + entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}