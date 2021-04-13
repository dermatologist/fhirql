# A FHIR has been lit on this server

## What is fhirql

Fhirql is a spring boot adaptation of hapi fhir server. This can be used as a template for extending generic FHIR server for specific use cases. See the example projects below. I have updated it to FHIR-R4 and spring-boot 2.2.

## FHIR® R4 (HL7 Fast Healthcare Interoperability Resources, Release 4)

## Other projects that using this as backend

* [:fire: The FHIRForm framework for managing healthcare eForms](https://github.com/E-Health/fhirform)
* [:eyes: Drishti | An mHealth sense-plan-act framework!](https://github.com/E-Health/drishti)

## Requirements

* java 13
* maven 3

## How to Use:

```
git clone https://github.com/dermatologist/fhirql.git
mvn spring-boot:run

```

* Access UI at http://localhost:8080/fhir and FHIR BASE at http://localhost:8080/fhir/fhir

## How to extend

* This uses spring boot Web.
* Override the default UI by adding files with the same name to WEB-INF/templates (Thymeleaf).
* For example this application overrides tmpl-head.html and tmpl-home-welcome.html
<<<<<<< HEAD
* The list of original templates are [here](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-testpage-overlay/src/main/webapp/WEB-INF/templates)
=======
* The list of original templates are [here](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-testpage-overlay/src/main/webapp/WEB-INF/templates)
>>>>>>> 323b10a965e646f0e5313b4aa6785b3c23053101

## Docker

Pre-build docker container of overlay branch is available for testing and can be deployed using the following command. Access it at http://localhost:8080/fhirql
(Docker container is for testing only.)

```
docker run -d --name fhirserver -p 8080:8080 beapen/fhir
```

## Author

[Bell Eapen](https://nuchange.ca)
