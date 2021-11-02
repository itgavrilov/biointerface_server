package ru.gsa.biointerface.repository.database;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.configuration.ApplicationConfiguration;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.repository.PatientRecordRepository;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientRecordService;

import javax.transaction.Transactional;


class DataSourceTest {

    @Test
    void getSessionFactory() {
        try (AnnotationConfigApplicationContext context
                     = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {

            PatientRecordService service = context.getBean(PatientRecordService.class);
            IcdService icdService = context.getBean(IcdService.class);

            try {
                Icd icd = new Icd("testName", 10, "testComment");
                icd = icdService.save(icd);

                System.out.println("!!!!!!!!!!"+icd);
                icdService.delete(icd);
                PatientRecord entity = service.findById(3L);

                System.out.println("!!!!!!!!!!"+entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}