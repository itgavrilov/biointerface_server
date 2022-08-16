package ru.gsa.biointerface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "ru.gsa.biointerface.config",
        "ru.gsa.biointerface.host",
        "ru.gsa.biointerface.service",
        "ru.gsa.biointerface.controller",
        "ru.gsa.biointerface.mapper",
        "ru.gsa.biointerface.repository"
})
@EntityScan("ru.gsa.biointerface.domain.entity")
@EnableJpaRepositories("ru.gsa.biointerface.repository")
public class RootConfig {

}
