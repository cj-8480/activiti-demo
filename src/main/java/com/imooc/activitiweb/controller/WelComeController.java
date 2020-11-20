package com.imooc.activitiweb.controller;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class WelComeController {

    @GetMapping("/")
    public String logPage() {
        return "redirect:/layuimini/page/login-1.html";
    }
}
