package com.app.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {

    @RequestMapping("/app")
    String index(){
        return "index";
    }
    @RequestMapping("/login")
    String login(){
        return "login";
    }

}
