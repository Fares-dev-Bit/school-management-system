package com.app.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final AppProperties appProperties;

    @Autowired
    public GlobalModelAttributes(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @ModelAttribute("appName")
    public String appName() {
        return appProperties.getName();
    }

    @ModelAttribute("appSubtitle")
    public String appSubtitle() {
        return appProperties.getSubtitle();
    }

    @ModelAttribute("appVendor")
    public String appVendor() {
        return appProperties.getVendor();
    }
}