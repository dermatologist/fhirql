# A FHIR has been lit on this server

## Branches

* fhir-server : The basic FHIR Restful server using spring-boot and UHN's hapi-fhir library.
* overlay: FHIR server with the client overlay using spring-boot
* injector: Injects resources from a file.
* interceptor: Injects a DataElement demap extension reference into a Questionnaire resource.
* develop: main development branch

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

* Access at http://localhost:8080/fhir

## Docker

Pre-build docker container of overlay branch is available for testing and can be deployed using the following command. Access it at http://localhost/fhirql
(Docker container is for testing only.)

```
docker run -d --name fhirserver -p 80:8080 beapen/fhir
```

## Author

Bell Eapen (McMaster U)
