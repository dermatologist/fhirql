package com.canehealth.injector;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.canehealth.config.DataConfig;
import com.canehealth.service.InjectorService;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Reads a FHIR resource bundle from the classpath and sends it to the server
 */

public class ResourceInjector implements DataInjector {

    //Resource is read from this file (Must be available in the classpath)
    public final String sourceFile;
    private final Logger log = LoggerFactory.getLogger(ResourceInjector.class);
    private final boolean useUpdate;

    @Autowired
    protected InjectorService injectorService;

    public ResourceInjector(Map<String, String> parameters) {
        this.sourceFile = parameters.get(DataConfig.SET_FILE);
        useUpdate = Boolean.parseBoolean(parameters.get(DataConfig.INJECTOR_PROP_USE_UPDATE));
    }


    @Override
    public void inject(IGenericClient client) {
        log.info("About to inject: {}", sourceFile);
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser;
        if (sourceFile.toLowerCase().endsWith(".xml")) {
            parser = ctx.newXmlParser();
        } else {
            parser = ctx.newJsonParser();
        }
        parser.setPrettyPrint(true);

        IBaseResource resource;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(sourceFile).toString()).getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            resource = parser.parseResource(reader);

            // beapen: If resource is a questionnaire, apply dataelement injector
            if (resource.getClass() == Questionnaire.class) {
                Questionnaire questionnaire = (Questionnaire) resource;
                resource = injectorService.processQuestionnaire(questionnaire);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data file", e);
        }
        try {
            if (useUpdate) {
                client.update().resource(resource).execute();
            } else {
                client.create().resource(resource).prettyPrint().encodedJson().execute();
            }
            log.info("Finished injecting: {}", sourceFile);
        }catch(Exception e){
            log.info("Rest Server not initiated");
        }
    }

}