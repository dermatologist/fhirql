package com.canehealth.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class InjectorService {
    private final Logger log = LoggerFactory.getLogger(InjectorService.class);
    private final IGenericClient fhirClient;
    @Value("${spring.application.uri}")
    protected String uri = "http://canehealth.com/fhirform/";
    @Value("${spring.application.demap}")
    protected String demap = "http://hl7.org/fhir/StructureDefinition/questionnaire-deMap";

    public InjectorService(IGenericClient fhirClient) {
        super();
        this.fhirClient = fhirClient;
    }

    /* function to check if url valid */
    private static boolean urlValidator(String url) {
        /*validating url*/
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException exception) {
            return false;
        } catch (MalformedURLException exception) {
            return false;
        }
    }

    public Questionnaire processQuestionnaire(Questionnaire questionnaire) {

        log.info("Processing Questionnaire: {}", questionnaire.getId());
        List<Questionnaire.QuestionnaireItemComponent> empty_list = new ArrayList<>();
        Questionnaire newQ = questionnaire.copy();
        newQ.setItem(empty_list);
        if (questionnaire.getItem().isEmpty())
            return questionnaire;

        for (Questionnaire.QuestionnaireItemComponent itemComponent : questionnaire.getItem()) {
            newQ = processItems(newQ, itemComponent);
        }

        return newQ;
    }

    private Questionnaire processItems(Questionnaire questionnaire, Questionnaire.QuestionnaireItemComponent itemComponent) {

        if (inspect(itemComponent)) {
            List<Extension> extensions = getDemaps(itemComponent);
            questionnaire = processExtensions(questionnaire, extensions);
        } else if (itemComponent.getType() == Questionnaire.QuestionnaireItemType.GROUP) {
            for (Questionnaire.QuestionnaireItemComponent item : itemComponent.getItem()) {
                processItems(questionnaire, item);
            }
        } else {
            questionnaire.addItem(itemComponent);
        }
        return questionnaire;
    }

    private Questionnaire processExtensions(Questionnaire questionnaire, List<Extension> extensions) {
        for (Extension extension : extensions) {
            IBaseDatatype iBaseDatatype = extension.getValue();
            Reference reference = (Reference) iBaseDatatype;
            String extension_value = reference.getReference();
            log.info("Reading Extension: {}", extension_value);
            if (extension_value.contains(uri)) {
                return injectFile(questionnaire, extension_value, uri);
            } else {
                return injectLocalDataElement(questionnaire, extension_value, uri);
            }
        }
        return questionnaire;
    }

    private List<Extension> getDemaps(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> extensions = new ArrayList();
        if (item.getExtensionsByUrl(demap) != null) {
            extensions = item.getExtensionsByUrl(demap);
        }
        return extensions;
    }

    public Questionnaire inject(Questionnaire questionnaire, DataElement dataElement) {
        for (Resource res : dataElement.getContained()) {
            if (res.getClass() == ValueSet.class || res.getClass() == DataElement.class)
                questionnaire.addContained(res);
            if (res.getClass() == Questionnaire.class) {
                Questionnaire qu = (Questionnaire) res;
                for (Questionnaire.QuestionnaireItemComponent qi : qu.getItem()) {
                    questionnaire.addItem(qi);
                }
            }
        }
        return questionnaire;
    }

    public boolean inspect(Questionnaire.QuestionnaireItemComponent item) {
        return item.getExtensionsByUrl(demap) != null;
    }

    private Questionnaire injectFile(Questionnaire questionnaire, String extension_value, String uri) {
        String sourceFile = extension_value.replace(uri, "FHIRForms/").concat(".json");
        // The segment below is from ResourceInjector.java
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
            // In this case the resource is a DataElement
            DataElement dataElement = (DataElement) resource;
            questionnaire = inject(questionnaire, dataElement);

        } catch (final IOException e) {
            throw new RuntimeException("Unable to read data file", e);
        }
        return questionnaire;
    }

    private Questionnaire injectLocalDataElement(Questionnaire questionnaire, String extension_value, String uri) {
        log.info("Reading Extension: {}", extension_value);
        DataElement dataElement;
        if (urlValidator(uri)) {
            dataElement = fhirClient.read().resource(DataElement.class).withUrl(uri).execute();
        } else {
            dataElement = fhirClient.read().resource(DataElement.class).withId(uri).execute();
        }
        log.info("About to inject: {}", uri);
        final FhirContext ctx = FhirContext.forDstu3();
        ctx.setParserErrorHandler(new StrictErrorHandler());
        final IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        questionnaire = inject(questionnaire, dataElement);
        return questionnaire;
    }
}
