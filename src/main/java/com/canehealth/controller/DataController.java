package com.canehealth.controller;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.canehealth.config.DataConfig;
import com.canehealth.config.DatasetInitializer;
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

//    @RequestMapping(value = "/fhir/baseDstu3/metadata", method = RequestMethod.POST)
//    public void initData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//        log.debug("Feeding data...");
//        //dataInitializer.feedData(dataInitializer.parseDatasets(dataConfig), fhirClient);
//        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//        log.debug("Done");
//    }
}