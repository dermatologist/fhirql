package com.canehealth.injector;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class DataElementInjector {
    private final Logger log = LoggerFactory.getLogger(DataElementInjector.class);

    @Value("${spring.application.uri}")
    private String uri = "http://canehealth.com/fhirform/";

    @Value("${spring.application.demap}")
    private String demap = "http://hl7.org/fhir/StructureDefinition/questionnaire-deMap";

    private Questionnaire newQ;

    /**
     * Injects the dataElement into a questionnaire if it contains demap extension
     */
    public Questionnaire inject(Questionnaire questionnaire) {

        log.info("Processing Questionnaire: {}", questionnaire.getId());
        List<Questionnaire.QuestionnaireItemComponent> empty_list = new ArrayList<>();
        this.newQ = questionnaire.copy();
        this.newQ.setItem(empty_list);
        List<Questionnaire.QuestionnaireItemComponent> newItems = new ArrayList<>();
        List<Questionnaire.QuestionnaireItemComponent> innerItems = new ArrayList<>();

        List<Questionnaire.QuestionnaireItemComponent> questionnaireItemComponent
                = questionnaire.getItem();

        if (questionnaireItemComponent.isEmpty())
            return questionnaire;

        for (Questionnaire.QuestionnaireItemComponent item : questionnaireItemComponent) {
            log.info("Processing Item: {}", item.getLinkId());
            if (item.getType() == Questionnaire.QuestionnaireItemType.GROUP) {
                Questionnaire.QuestionnaireItemComponent i = new Questionnaire.QuestionnaireItemComponent();
                i.setLinkId(item.getLinkId());
                i.setType(item.getType());
                i.setText(item.getText());
                i.setItem(empty_list);

                for (Questionnaire.QuestionnaireItemComponent group_item : item.getItem()) {
                    log.info("Processing Item: {}", group_item.getLinkId());
                    innerItems.add(inspect(group_item));
                }
                newItems.addAll(innerItems);
                i.setItem(newItems);
                this.newQ.addItem(i);
            } else {
                this.newQ.addItem(inspect(item));
            }
        }
        //this.newQ.setItem(newItems);
        return this.newQ;
    }


    public Questionnaire.QuestionnaireItemComponent inspect(Questionnaire.QuestionnaireItemComponent item) {
        List<Extension> extensions = new ArrayList();
        if (item.getExtensionsByUrl(demap) != null) {
            extensions = item.getExtensionsByUrl(demap);
        }
        for (Extension extension : extensions) {
            IBaseDatatype iBaseDatatype = extension.getValue();
            Reference reference = (Reference) iBaseDatatype;
            String extension_value = reference.getReference();

            // If demap extension value has uri, process it
            if (extension_value.contains(uri)) {
                log.info("Reading Extension: {}", extension_value);
                // Replace the uri with local path
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

                } catch (final IOException e) {
                    throw new RuntimeException("Unable to read data file", e);
                }
            }
        }
        return item;
    }

}

