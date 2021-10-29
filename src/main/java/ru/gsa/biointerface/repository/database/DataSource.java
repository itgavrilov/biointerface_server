package ru.gsa.biointerface.repository.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

import javax.persistence.PersistenceException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DataSource implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);
    private final SessionFactory sessionFactory;

    public DataSource(boolean test) throws NoConnectionException {
        try {
            Configuration cfg;

            if(test) {
                cfg = new Configuration()
                        .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                        .setProperty("hibernate.connection.url", "jdbc:h2:mem:test")
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                        .addAnnotatedClass(Sample.class)
                        .addAnnotatedClass(Channel.class)
                        .addAnnotatedClass(Examination.class)
                        .addAnnotatedClass(Device.class)
                        .addAnnotatedClass(ChannelName.class)
                        .addAnnotatedClass(PatientRecord.class)
                        .addAnnotatedClass(Icd.class);
            } else {
                cfg = new Configuration()
                        .addAnnotatedClass(Sample.class)
                        .addAnnotatedClass(Channel.class)
                        .addAnnotatedClass(Examination.class)
                        .addAnnotatedClass(Device.class)
                        .addAnnotatedClass(ChannelName.class)
                        .addAnnotatedClass(PatientRecord.class)
                        .addAnnotatedClass(Icd.class);
            }

            this.sessionFactory = cfg.buildSessionFactory();
            LOGGER.info("Successful database connection");
        } catch (PersistenceException e) {
            LOGGER.error("Error connecting to database", e);
            throw new NoConnectionException(e);
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
