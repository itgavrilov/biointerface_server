package ru.gsa.biointerface.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.springdoc.core.SpringDocUtils.getConfig;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10/01/2022
 */
@Slf4j
@Configuration
public class SwaggerConfiguration {

    @PostConstruct
    public void init() {
        getConfig().replaceWithClass(org.springframework.data.domain.Pageable.class,
                org.springdoc.core.converters.models.Pageable.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("API biointerface server")
                .description("REST API сервера биоинтерфейсов"));
    }
}
