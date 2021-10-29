package ru.gsa.biointerface;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.gsa.biointerface.domain.entity.*;
import ru.gsa.biointerface.repository.database.DataSource;
import ru.gsa.biointerface.repository.exception.NoConnectionException;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/10/2021
 */
@Configuration
@ComponentScan("ru.gsa.biointerface.repository")
@ComponentScan("ru.gsa.biointerface.service")
public class SpringConfig {
    @Bean()
    SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration()
//                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
//                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test")
//                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .addAnnotatedClass(Sample.class)
                .addAnnotatedClass(Channel.class)
                .addAnnotatedClass(Examination.class)
                .addAnnotatedClass(Device.class)
                .addAnnotatedClass(ChannelName.class)
                .addAnnotatedClass(PatientRecord.class)
                .addAnnotatedClass(Icd.class);

        return cfg.buildSessionFactory();
    }
}
