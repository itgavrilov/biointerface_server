package ru.gsa.biointerface.repository.database;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.config.JpaConfig;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;


class DataSourceTest {

    @Test
    void getSessionFactory() {
        try (AnnotationConfigApplicationContext context
                     = new AnnotationConfigApplicationContext(JpaConfig.class)) {

            PatientService service = context.getBean(PatientService.class);
            IcdService icdService = context.getBean(IcdService.class);

            try {
                Icd icd = new Icd("testName", 10, "testComment");
                icd = icdService.save(icd);

                System.out.println("!!!!!!!!!!" + icd);
                icdService.delete(icd);
                Patient entity = service.findById(3);

                System.out.println("!!!!!!!!!!" + entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}