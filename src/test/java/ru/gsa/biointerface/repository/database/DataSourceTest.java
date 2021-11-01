package ru.gsa.biointerface.repository.database;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.configuration.ApplicationConfiguration;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.service.PatientRecordService;


class DataSourceTest {

    @Test
    void getSessionFactory() {
        try (AnnotationConfigApplicationContext context
                     = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            //PatientRecordService service = context.getBean("patientRecordService", PatientRecordService.class);
            PatientRecordService service = context.getBean(PatientRecordService.class);

            try {
                PatientRecord entity = service.getById((long) 3);
                System.out.println(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}