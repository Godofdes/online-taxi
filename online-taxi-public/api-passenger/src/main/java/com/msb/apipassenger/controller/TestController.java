package com.msb.apipassenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/testInter")
    public String test1(){
        System.out.println("yessssss");
        return "111";
    }
}
