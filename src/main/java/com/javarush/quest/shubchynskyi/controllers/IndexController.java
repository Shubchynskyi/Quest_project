package com.javarush.quest.shubchynskyi.controllers;


import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.util.Go;
import com.javarush.quest.shubchynskyi.util.Jsp;
import com.javarush.quest.shubchynskyi.util.Key;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

//@WebServlet(name = "IndexServlet", value = Go.ROOT, loadOnStartup = 0)
@Controller
public class IndexController {

//    @Autowired
//    public IndexController(ServletContext servletContext) {
//        this.servletContext = servletContext;
//        servletContext.setAttribute(Key.ROLES, Role.values());
//    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

}
