package ru.gsa.biointerface.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07/11/2021
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.gsa.biointerface.controller")
public class MvcConfig implements WebMvcConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvcConfig.class);

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp().prefix("/WEB-INF/jsp/");
        registry.jsp().suffix(".jsp");
        WebMvcConfigurer.super.configureViewResolvers(registry);
        LOGGER.info("Configure ViewResolvers");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/WEB-INF/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/WEB-INF/favicon.ico");
        WebMvcConfigurer.super.addResourceHandlers(registry);
        LOGGER.info("Add resourceHandlers");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("index");
//        WebMvcConfigurer.super.addViewControllers(registry);
//    }

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable("dispatcherServlet");
//    }
}
