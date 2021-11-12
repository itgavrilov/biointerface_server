package ru.gsa.biointerface.controller;

import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class CustomErrorController {
//public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError() {
        return "error";
    }

    //@Override
    public String getErrorPath() {
        return "/error";
    }
}
