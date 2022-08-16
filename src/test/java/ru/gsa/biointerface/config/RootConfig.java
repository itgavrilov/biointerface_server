package ru.gsa.biointerface.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.mapper.IcdMapperImpl;

@Configuration
public class RootConfig {

    @Bean
    public IcdMapper getMapper() {
        return new IcdMapperImpl();
    }
}
