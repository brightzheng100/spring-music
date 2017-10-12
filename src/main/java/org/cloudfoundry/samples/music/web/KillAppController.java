package org.cloudfoundry.samples.music.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KillAppController {

    @RequestMapping(value="/killApp")
    public @ResponseBody
    String kill(){
        System.exit(-1);
        return "Killed";
    }
}