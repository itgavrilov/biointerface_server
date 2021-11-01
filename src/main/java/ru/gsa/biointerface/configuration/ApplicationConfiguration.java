package ru.gsa.biointerface.configuration;

import java.util.Objects;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/10/2021
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("ru.gsa.biointerface")
@EnableJpaRepositories(basePackages = "ru.gsa.biointerface.repository")
@PropertySource("classpath:hibernate.properties")
public class ApplicationConfiguration {
    @Autowired
    private Environment env;

    private String getProperty(String key) {
        return Objects.requireNonNull(env.getProperty(key));
    }

    private Properties getProperties() {
        Properties connectionProperties = new Properties();
        connectionProperties.put("hibernate.dialect", getProperty("hibernate.dialect"));

        return connectionProperties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getProperty("hibernate.connection.driver_class"));
        dataSource.setUrl(getProperty("hibernate.connection.url"));
        dataSource.setConnectionProperties(getProperties());

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setPackagesToScan("ru.gsa.biointerface.domain.entity");
        factory.setDataSource(dataSource);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);

        return txManager;
    }

//    @Bean
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setPackagesToScan("ru.gsa.biointerface.domain.entity");
//        sessionFactory.setDataSource(dataSource());
//
//        return sessionFactory;
//    }
//
//    @Bean
//    public PlatformTransactionManager hibernateTransactionManager() {
//        HibernateTransactionManager transactionManager
//                = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory().getObject());
//
//        return transactionManager;
//    }
}
