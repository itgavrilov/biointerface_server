package ru.gsa.biointerface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("ru.gsa.biointerface.controller")
public class ControllerConfig {
}
