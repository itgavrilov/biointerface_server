package ru.gsa.biointerface.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.mapper.IcdMapperImpl;

@TestConfiguration
public class TestConfig {

    @Bean
    public IcdMapper getMapper() {
        return new IcdMapperImpl();
    }
}
