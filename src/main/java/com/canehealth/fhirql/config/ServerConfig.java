package com.canehealth.fhirql.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.server.IServerAddressStrategy;
import ca.uhn.fhir.rest.server.IncomingRequestAddressStrategy;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import com.canehealth.fhirql.interceptor.DataElementResponseInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@Import(BaseJavaConfigR4.class)
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
        final IRestfulClientFactory factory = FhirContext.forR4().getRestfulClientFactory();

        factory.setConnectTimeout(config.timeout);
        factory.setConnectionRequestTimeout(config.timeout);
        factory.setSocketTimeout(config.timeout);
        return factory.newGenericClient("http://localhost:" + port + "/" + contextPath + "/" + config.path);
    }

    @Bean
    public FhirConfig fhirConfig() {
        return new FhirConfig(FhirVersionEnum.R4, "baseR4", 60000);
    }

    @Bean
    public IServerInterceptor dataElementResponseInterceptor() {
        return new DataElementResponseInterceptor();
    }

}

