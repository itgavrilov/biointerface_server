package ru.gsa.biointerface.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 05/11/2021
 */
@Controller
@RequestMapping(value = "/")
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @GetMapping
    public String loadHomePage(Model model) {
        System.out.println("sout - Load home page");
        LOGGER.info("LOGGER - Load home page");
        model.addAttribute("message", "Hello World!");

        return "index";
    }
}
