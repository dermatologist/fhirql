package com.canehealth.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigDstu3;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.server.IServerAddressStrategy;
import ca.uhn.fhir.rest.server.IncomingRequestAddressStrategy;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import com.canehealth.interceptor.DataElementResponseInterceptor;
import org.hl7.fhir.dstu3.model.MetadataResource;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;

@Configuration
@Import(BaseJavaConfigDstu3.class)
public class ServerConfig {

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }


    @Bean
    public IServerAddressStrategy serverAddressStrategy() {
        return new IncomingRequestAddressStrategy();
    }

    /**
     * The server can access itself with this client
     *
     * @param config
     * @param port
     * @param contextPath
     * @return
     */
    @Bean
    public IGenericClient fhirClient(FhirConfig config, @Value("${server.port}") String port,
                                     @Value("${server.contextPath}") String contextPath) {
        final IRestfulClientFactory factory = FhirContext.forDstu3().getRestfulClientFactory();

        factory.setConnectTimeout(config.timeout);
        factory.setConnectionRequestTimeout(config.timeout);
        factory.setSocketTimeout(config.timeout);
        return factory.newGenericClient("http://localhost:" + port + "/" + contextPath + "/" + config.path);
    }

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.DSTU3, "baseDstu3", 60000);
    }

    @Bean
    public IServerInterceptor dataElementResponseInterceptor() {
        return new DataElementResponseInterceptor();
    }


//    @Bean(autowire = Autowire.BY_TYPE)
//    public IServerInterceptor subscriptionSecurityInterceptor() {
//        return new SubscriptionsRequireManualActivationInterceptorDstu3();
//    }

    private <E extends MetadataResource> E loadResource(String file, IParser parser) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(file).toString()).getInputStream(), Charset.forName("UTF-8")))) {
            return (E) parser.parseResource(reader);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read profile file", e);
        }
    }

    private StructureDefinition loadStructureDefinition(String file, IParser parser) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(Paths.get(file).toString()).getInputStream(), Charset.forName("UTF-8")))) {
            return (StructureDefinition) parser.parseResource(reader);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read profile file", e);
        }
    }

}

