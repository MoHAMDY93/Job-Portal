package com.hamdy.jobPortal.company.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logging")
@Slf4j
public class LoggingController {

    @GetMapping(path = "/public" , version = "1.0")
    public ResponseEntity<String> testLogging() {
        log.trace("TRACE😂");
        log.debug("DEBUG😭");
        log.info("INFO🎶");
        log.warn("WARN😁");
        log.error("ERROR😑");

        return ResponseEntity.ok().body("Logging tested successfully");
    }
}
