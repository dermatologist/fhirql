package com.canehealth.config;

import ca.uhn.fhir.context.FhirVersionEnum;

public class FhirConfig {

    public final FhirVersionEnum versionEnum;

    public final String path;

    public final int timeout;

    public FhirConfig(FhirVersionEnum versionEnum, String path, int timeout) {
        this.timeout = timeout;
        this.versionEnum = versionEnum;
        this.path = path;
    }

}
