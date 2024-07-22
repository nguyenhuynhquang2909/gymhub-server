package com.gymhub.gymhub.controller;

import java.util.Map;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@RequestMapping("/mappings")
public class MappingsController {
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping
    public Map<RequestMappingInfo, HandlerMethod> getAllMappings() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        return requestMappingHandlerMapping.getHandlerMethods();
    }
}
