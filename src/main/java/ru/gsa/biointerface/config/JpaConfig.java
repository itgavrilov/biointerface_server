package ru.gsa.biointerface.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ru.gsa.biointerface.repository")
@EntityScan("ru.gsa.biointerface.domain.entity")
public class JpaConfig {
}
