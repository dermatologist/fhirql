package com.canehealth.fhirql.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ca.uhn.fhir.to"},
        excludeFilters = {
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ca.uhn.fhir.to.FhirTesterMvcConfig.class)})
public class FhirTesterMvcConfig implements WebMvcConfigurer {
    public FhirTesterMvcConfig() {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/css/");
        registry.addResourceHandler("/fa/**").addResourceLocations("classpath:/fa/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/fonts/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/img/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
        registry.addResourceHandler("/profiles/**").addResourceLocations("classpath:/profiles/");

    }
}