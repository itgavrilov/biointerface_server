package ru.gsa.biointerface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Description;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07/11/2021
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.gsa.biointerface.config",
        "ru.gsa.biointerface.host",
        "ru.gsa.biointerface.service",
        "ru.gsa.biointerface.controller",
        "ru.gsa.biointerface.mapper",
        "ru.gsa.biointerface.repository"
})
public class WebAppInit extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebAppInit.class, args);
    }

    @Override
    @Description("To run on an external container without Spring Framework Servlet 3.0")
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebAppInit.class);
    }
}