package com.javarush.quest.shubchynskyi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.javarush.quest.shubchynskyi.util.constant.Route.INDEX;

@Controller
public class IndexController {

    @GetMapping(INDEX)
    public String getIndex() {
        return "/index";
    }

}
