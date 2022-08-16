package ru.gsa.biointerface.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.mapper.IcdMapperImpl;

@TestConfiguration
public class RootConfig {

    @Bean
    public IcdMapper getMapper() {
        return new IcdMapperImpl();
    }
}
