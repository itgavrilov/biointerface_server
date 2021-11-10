package ru.gsa.biointerface.config;

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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 29/10/2021
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({"ru.gsa.biointerface.service", "ru.gsa.biointerface.host"})
@EnableJpaRepositories("ru.gsa.biointerface.repository")
@PropertySource("classpath:application.properties")
public class JpaConfig {
    //private static final Logger LOGGER = LoggerFactory.getLogger(JpaConfig.class);

    @Autowired
    private Environment env;

    private String getProperty(String key) {
        return Objects.requireNonNull(env.getProperty(key));
    }

    private Properties getFactoryProperties() {
        Properties connectionProperties = new Properties();
        connectionProperties.put("hibernate.dialect", getProperty("hibernate.dialect"));
        connectionProperties.put("hibernate.hbm2ddl.auto", getProperty("hibernate.hbm2ddl.auto"));
        connectionProperties.put("hibernate.connection.autocommit", getProperty("hibernate.connection.autocommit"));
        connectionProperties.put("hibernate.cache.use_second_level_cache", getProperty("hibernate.cache.use_second_level_cache"));
        connectionProperties.put("hibernate.cache.provider_class", getProperty("hibernate.cache.provider_class"));
        connectionProperties.put("hibernate.format_sql", getProperty("hibernate.format_sql"));
        connectionProperties.put("hibernate.show_sql", getProperty("hibernate.show_sql"));

        return connectionProperties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getProperty("hibernate.connection.driver_class"));
        dataSource.setUrl(getProperty("hibernate.connection.url"));
        dataSource.setUsername(getProperty("hibernate.connection.username"));
        dataSource.setPassword(getProperty("hibernate.connection.password"));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        vendorAdapter.setGenerateDdl(true);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setPackagesToScan("ru.gsa.biointerface.domain.entity");
        factory.setDataSource(dataSource);
        factory.setJpaProperties(getFactoryProperties());

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);

        return txManager;
    }
}
