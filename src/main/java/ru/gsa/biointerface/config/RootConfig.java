package ru.gsa.biointerface.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 18/11/2021
 */
@Slf4j
@Configuration
public class RootConfig {
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {

            @Override
            public void configure(@NonNull ObjectMapper objectMapper) {
                super.configure(objectMapper);
                objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
                objectMapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.PUBLIC_ONLY);
                log.info("Configure ObjectMapper for Jackson");
            }

        };
    }
}
