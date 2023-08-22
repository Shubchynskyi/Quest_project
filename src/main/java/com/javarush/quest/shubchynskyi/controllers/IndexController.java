package com.javarush.quest.shubchynskyi.controllers;

import com.javarush.quest.shubchynskyi.constant.Key;
import com.javarush.quest.shubchynskyi.constant.Route;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;

@Controller
public class IndexController {

    @GetMapping(INDEX)
    public String getIndex(Model model) {
        model.addAttribute(Key.TITLE_KEY, Key.TITLE_INDEX);
        return Route.INDEX_PAGE;
    }

}
