package com.canehealth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "app.data")
public class DataConfig {
    public static final String SET_DESCRIPTION = "description";

    public static final String SET_FILE = "file";

    public static final String SET_ORDER = "order";

    public static final String SET_INJECTOR_PROPS = "injector_properties";

    public static final String SET_INJECTOR_CLASS = "injector";

    public static final String INJECTOR_PROP_USE_UPDATE = "use_update";

    public static final String HEADERS = "headers";

    private List<Map<String, String>> resources;

    private Map<String, String> headers;

    public List<Map<String, String>> getResources() {
        return resources;
    }

    public void setResources(List<Map<String, String>> resources) {
        this.resources = resources;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}