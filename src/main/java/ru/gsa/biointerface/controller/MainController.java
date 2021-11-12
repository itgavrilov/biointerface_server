package ru.gsa.biointerface.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 05/11/2021
 */
@Slf4j
@Controller
@RequestMapping(value = "/")
public class MainController {

    @GetMapping(value = "")
    public String loadHomePage(Model model) {
        log.info("LOGGER - Load home page");
        model.addAttribute("message", "Hello World!");

        return "index";
    }
}
