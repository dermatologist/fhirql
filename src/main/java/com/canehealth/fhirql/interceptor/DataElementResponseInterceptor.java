package com.canehealth.fhirql.interceptor;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import com.canehealth.fhirql.service.InjectorService;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.canehealth.fhirql")
public class DataElementResponseInterceptor extends InterceptorAdapter {

    @Autowired
    private InjectorService injectorService;

    @Override
    public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
        if (theResponseObject instanceof Resource) {
            if (theResponseObject.getClass() == Questionnaire.class) {
                Questionnaire questionnaire = (Questionnaire) theResponseObject;
                theResponseObject = injectorService.processQuestionnaire(questionnaire);
            }
        }

        return true;
    }
}
