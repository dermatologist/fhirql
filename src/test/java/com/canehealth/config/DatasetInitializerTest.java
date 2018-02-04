package com.canehealth.config;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import com.canehealth.injector.DataInjector;
import com.canehealth.injector.ResourceInjector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


public class DatasetInitializerTest {

    private final String resourceInjector = "com.canehealth.injector.ResourceInjector";
    private final String foobarInjector = "com.canehealth.injector.Foobar";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private IGenericClient fhirClient;
    private DatasetInitializer datasetInitializer;
    private DataConfig dataConfig;
    private List<Map<String, String>> injectors;
    private Map<String, String> resourceInjectorConfig;

    private Map<String, String> foobarInjectorConfig;

    @Before
    public void setUp() {
        fhirClient = mock(GenericClient.class);
        resourceInjectorConfig = new HashMap<String, String>();
        foobarInjectorConfig = new HashMap<String, String>();

        resourceInjectorConfig.put("file", "datafile.json");
        resourceInjectorConfig.put("injector", resourceInjector);

        foobarInjectorConfig.put("file", "datafile.json");
        foobarInjectorConfig.put("injector", foobarInjector);

    }

    @Test
    public void parseValidInjectors() {
        dataConfig = new DataConfig();
        injectors = asList(resourceInjectorConfig);
        dataConfig.setResources(injectors);
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        List<DataInjector> sets = datasetInitializer.parseDatasets(dataConfig);
        assertThat(sets.size(), equalTo(1));
        assertThat(sets, hasItem(isA(ResourceInjector.class)));

    }

    @Test
    public void parseInvalidInjector() {
        dataConfig = new DataConfig();
        injectors = asList(foobarInjectorConfig);
        dataConfig.setResources(injectors);
        thrown.expect(RuntimeException.class);
        thrown.expectCause(isA(ClassNotFoundException.class));
        thrown.expectMessage("java.lang.ClassNotFoundException: com.canehealth.injector.Foobar");
        datasetInitializer = new DatasetInitializer(fhirClient, dataConfig, "true");
        datasetInitializer.parseDatasets(dataConfig);

    }

}
