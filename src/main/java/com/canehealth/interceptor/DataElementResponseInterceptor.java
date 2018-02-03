package com.canehealth.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class DataElementResponseInterceptor extends InterceptorAdapter {

    private DataElementInterceptor dataElementInterceptor = new DataElementInterceptor();

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof Resource) {
            // beapen: If resource is a questionnaire, apply dataelement injector
            if (theResponseObject.getClass() == Questionnaire.class) {
                Questionnaire questionnaire = (Questionnaire) theResponseObject;
                theResponseObject = dataElementInterceptor.inject(questionnaire);
            }
        }

        return true;
    }
}
