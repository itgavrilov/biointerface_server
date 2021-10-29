package ru.gsa.biointerface.repository.database;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.service.PatientRecordService;


class DataSourceTest {

    @Test
    void getSessionFactory() {
        try(
                ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml"
                )
        ) {
            PatientRecordService service = context.getBean("patientRecordService", PatientRecordService.class);

            try {
                PatientRecord entity = service.getById((long)3);
                System.out.println(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}