package com.canehealth.fhirql.controller;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.canehealth.fhirql.config.DataConfig;
import com.canehealth.fhirql.config.DatasetInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
    private final Logger log = LoggerFactory.getLogger(DataController.class);
    private DatasetInitializer dataInitializer;
    private DataConfig dataConfig;
    private IGenericClient fhirClient;

    public DataController(DatasetInitializer dataInitializer, IGenericClient fhirClient, DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        this.fhirClient = fhirClient;
        this.dataInitializer = dataInitializer;
    }

}