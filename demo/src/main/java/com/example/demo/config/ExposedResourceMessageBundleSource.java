package com.example.demo.config;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {

    public Properties getExposedMergedProperties(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}
