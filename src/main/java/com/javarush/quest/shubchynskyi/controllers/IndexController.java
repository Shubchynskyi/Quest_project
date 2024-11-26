package com.javarush.quest.shubchynskyi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.javarush.quest.shubchynskyi.constant.Route.INDEX_PAGE;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;

@Controller
public class IndexController {

    @GetMapping(INDEX)
    public String getIndex() {
        return INDEX_PAGE;
    }

}