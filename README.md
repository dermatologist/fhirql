# A FHIR has been lit on this server

## What is fhirql

Fhirql is a spring boot adaptation of hapi fhir server. This can be used as a template for extending generic FHIR server for specific use cases. See the example projects below. I have updated it to FHIR-R4 and spring-boot 2.2. 


## Other projects using this as server

* [:fire: The FHIRForm framework for managing healthcare eForms](https://github.com/E-Health/fhirform)
* [:eyes: Drishti | An mHealth sense-plan-act framework!](https://github.com/E-Health/drishti)

## Requirements

* java 8
* maven 3

## How to Use:

```
git clone https://github.com/dermatologist/fhirql.git
mvn spring-boot:run
```

* Access at http://localhost:8080/

## Docker

Pre-build docker container of overlay branch is available for testing and can be deployed using the following command. Access it at http://localhost:8080/fhirql
(Docker container is for testing only.)

```
docker run -d --name fhirserver -p 8080:8080 beapen/fhir
```

## Author

Bell Eapen (McMaster U)
