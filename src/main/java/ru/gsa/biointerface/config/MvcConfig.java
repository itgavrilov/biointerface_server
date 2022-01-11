package ru.gsa.biointerface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07/11/2021
 */
@Slf4j
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/");
        registry.addResourceHandler("/img/**").addResourceLocations("/WEB-INF/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/WEB-INF/favicon.ico");
        WebMvcConfigurer.super.addResourceHandlers(registry);
        log.info("Add resourceHandlers");
    }
//************************************************************************************************
//    Для ручной настройки папок Thymeleaf.
//    @Bean
//    @Primary
//    @Description("Template resolver for Thymeleaf")
//    public SpringResourceTemplateResolver templateResolver() {
//        final SpringResourceTemplateResolver templateResolver =
//                new SpringResourceTemplateResolver();
//        templateResolver.setPrefix("/WEB-INF/templates/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        templateResolver.setCharacterEncoding("UTF-8");
//        templateResolver.setOrder(1);
//        templateResolver.setCacheable(false);
//        //viewResolver.setCheckExistence(true);
//
//        return templateResolver;
//    }
//
//    @Bean
//    @Description("Template engine with Spring integration for Thymeleaf")
//    public SpringTemplateEngine templateEngine() {
//        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.setEnableSpringELCompiler(true);
//        templateEngine.addTemplateResolver(templateResolver());
////        templateEngine.addDialect(new SpringSecurityDialect());
////        templateEngine.addDialect(new LayoutDialect());
//
//        return templateEngine;
//    }
//
//    @Bean
//    @Description("Thymeleaf view resolver")
//    public ViewResolver getViewResolver() {
//        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setTemplateEngine(templateEngine());
//        viewResolver.setCharacterEncoding("UTF-8");
//        viewResolver.setOrder(1);
//        log.info("Getting internalResourceViewResolver");
//
//        return viewResolver;
//    }
//************************************************************************************************
//    для JSP. JSP не поддерживается Spring Boot
//    @Bean
//    @Description("JSP view resolver")
//    public ViewResolver getViewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setViewClass(JstlView.class);
//        viewResolver.setPrefix("/WEB-INF/jsp/");
//        viewResolver.setSuffix(".jsp");
//        viewResolver.setCache(false);
//
//        log.info("Getting internalResourceViewResolver");
//
//        return viewResolver;
//    }

//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.jsp().viewClass(JstlView.class);
//        registry.jsp().prefix("/WEB-INF/jsp/");
//        registry.jsp().suffix(".jsp");
//        registry.jsp().cache(false);
//
//        WebMvcConfigurer.super.configureViewResolvers(registry);
//        log.info("Configure ViewResolvers");
//    }

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable("dispatcherServlet");
//    }
//************************************************************************************************

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("index");
//        WebMvcConfigurer.super.addViewControllers(registry);
//    }
}
