package com.serezka.eljurbot.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Log4j2
@RequestMapping("/web")
public class WebController {

    @GetMapping(value = "/login ")
    public String login(Model model) {
        return "login";
    }
}
