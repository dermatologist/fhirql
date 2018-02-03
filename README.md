# A FHIR has been lit on this server

## Branches

* fhir-server : The basic FHIR Restful server using spring-boot and UHN's hapi-fhir library.
* overlay: FHIR server with the client overlay using spring-boot
* injector: Injects resources from a file.
* interceptor: Injects a DataElement demap extension reference into a Questionnaire resource.
* editor: Adds FRED (Fhir resource editor) to the fhir-server. FRED is a project by 'Smart Health IT' ( https://github.com/smart-on-fhir/fred ). Within this ecosystem, FRED can edit resources on the server. (Work in progress)
* develop: main development branch
* ... expect in the future:
* graphql: A Graphql adapter for 

## Requirements

* java 8
* maven 3

## How to Use:

```
git clone https://github.com/dermatologist/fhirql.git
mvn spring-boot:run
```

* Access at http://localhost:8080/fhir