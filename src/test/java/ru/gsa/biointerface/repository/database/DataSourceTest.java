package ru.gsa.biointerface.repository.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.configuration.ApplicationConfiguration;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.PatientRecord;
import ru.gsa.biointerface.service.PatientRecordService;


class DataSourceTest {
    @Test
    public void connection() {
        try (AnnotationConfigApplicationContext context
                     = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {
            SessionFactory sessionFactory = context.getBean(SessionFactory.class);

            Assertions.assertTrue(sessionFactory.isOpen());

            Device deviceExpected = new Device();
            deviceExpected.setId(1000);
            deviceExpected.setComment("test");

            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.saveOrUpdate(deviceExpected);
                transaction.commit();
            }

            try (Session session = sessionFactory.openSession()) {
                Device device1 = session.get(Device.class, 1000L);
                Assertions.assertEquals(deviceExpected.getId(), device1.getId());
                Assertions.assertEquals(deviceExpected.getComment(), device1.getComment());
            }
        }
    }
}