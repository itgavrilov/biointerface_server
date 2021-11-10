package ru.gsa.biointerface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.gsa.biointerface.config.MvcConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07/11/2021
 */
public class WebAppInit implements WebApplicationInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAppInit.class);

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext applicationContext =
                new AnnotationConfigWebApplicationContext();
        applicationContext.register(MvcConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        ServletRegistration.Dynamic registration =
                servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
