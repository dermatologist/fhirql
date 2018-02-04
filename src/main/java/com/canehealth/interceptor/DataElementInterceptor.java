package com.canehealth.interceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.canehealth.injector.DataElementInjector;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataElementInterceptor extends DataElementInjector {

    private final Logger log = LoggerFactory.getLogger(DataElementInterceptor.class);

    @Autowired
    private final IGenericClient fhirClient;

    public DataElementInterceptor(IGenericClient fhirClient) {
        super();
        this.fhirClient = fhirClient;
    }

    @Override
    public Questionnaire.QuestionnaireItemComponent inspect(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> extensions = new ArrayList();
        if (item.getExtensionsByUrl(this.demap) != null) {
            extensions = item.getExtensionsByUrl(demap);
        }
        for (Extension extension : extensions) {
            IBaseDatatype iBaseDatatype = extension.getValue();
            Reference reference = (Reference) iBaseDatatype;
            String extension_value = reference.getReference();

            // If demap extension value has uri, process it
            if (extension_value.contains(uri)) {
                log.info("Reading Extension: {}", extension_value);
                DataElement dataElement = fhirClient.read().resource(DataElement.class).withUrl(uri).execute();
                log.info("About to inject: {}", uri);
                final FhirContext ctx = FhirContext.forDstu3();
                ctx.setParserErrorHandler(new StrictErrorHandler());
                final IParser parser = ctx.newJsonParser();
                parser.setPrettyPrint(true);

                for (Resource res : dataElement.getContained()) {
                    if (res.getClass() == ValueSet.class || res.getClass() == DataElement.class)
                        newQ.addContained(res);
                    if (res.getClass() == Questionnaire.class) {
                        Questionnaire qu = (Questionnaire) res;
                        for (Questionnaire.QuestionnaireItemComponent qi : qu.getItem()) {
                            return qi;
                        }
                    }
                }

            }
        }
        return item;
    }
}
