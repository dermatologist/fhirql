package com.canehealth.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.canehealth")
public class DataElementResponseInterceptor extends InterceptorAdapter {

    @Autowired
    private DataElementInterceptor dataElementInterceptor;

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof Resource) {
            if (theResponseObject.getClass() == Questionnaire.class) {
                Questionnaire questionnaire = (Questionnaire) theResponseObject;
                theResponseObject = dataElementInterceptor.inject(questionnaire);
            }
        }

        return true;
    }
}
