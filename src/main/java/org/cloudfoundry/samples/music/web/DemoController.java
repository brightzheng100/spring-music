package org.cloudfoundry.samples.music.web;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RequestMapping(value="/ping")
    @ResponseBody
    public String ping(){
        return "pong: " + new Date();
    }

    @RequestMapping(value="/killme")
    @ResponseBody
    public String kill(){
        System.exit(-1);
        return "Killed";
    }
}