package ru.gsa.biointerface.controller.http;

import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class HttpErrorController {
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
