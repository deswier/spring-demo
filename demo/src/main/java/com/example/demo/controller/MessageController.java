package com.example.demo.controller;

import com.example.demo.config.ExposedResourceMessageBundleSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Properties;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final ExposedResourceMessageBundleSource messageSource;

    public MessageController(ExposedResourceMessageBundleSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public Properties getMessages(@RequestParam(name = "lang", defaultValue = "ru") String lang) {
        return messageSource.getExposedMergedProperties(new Locale(lang));
    }
}
