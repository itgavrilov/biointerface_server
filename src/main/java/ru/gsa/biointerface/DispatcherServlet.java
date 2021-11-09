//package ru.gsa.biointerface;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07/11/2021
// */
//@WebServlet("/")
//public class DispatcherServlet extends HttpServlet {
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        System.out.println("ru.gsa.biointerface.DispatcherServlet -> doGet");
//        resp.setContentType("text/html");
//        PrintWriter writer = resp.getWriter();
//        try {
//            writer.println("<h2>Welcome to servlets</h2>");
//        } finally {
//            writer.close();
//        }
//    }
//}
