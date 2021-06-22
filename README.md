# Java command line application for searching JSON data

Current configuration of this application let the users search three types of inter-related data (users, organizations and tickets) and display the relevant data combinations where applicable. This application based on couple of main components,

Searchable data parser module

Configurable data display module

These modules built in such a generic way which can be extendable to other valid Json data types easily.

I have also made an attempt to demonstrate some commonly used best practices and tools in Java development which is useful for building robust bug free code. They are Junit, Jacoco and maven for dependency management and build automation.

Following libraries are tools used in the project :

* Simple-json (v 1.1.1) for JSON data parsing
* JUnit 5 for unit testing
* JaCoCo for code coverage reporting
* Maven to building and putting it all together

## Building the project

prerequisites

* Java 11 SDK
* Apache Maven 3.8.1
* Internet access to download the dependencies

Once the project cloned go to the project root directory and run 

``` bash
mvn clean install
```


This will download all the dependencies, compile the classes, create all the necessary directories/copy config files, verify the build via running the unit tests and build an execute-aware Jar file under the 

'%project_toot%/target' directory

## Code coverage reports

Code coverage reports will be generated once `mvn verify` (or a full `mvn clean install`) is called. Open the `%project_toot%/target/site/jacoco/index.html` to see the report.

## Running the unit tests independently

To run the unit tests, call 

``` bash
mvn test
```

## Running the application

From the '%project_root%' directory, then type 

``` bash
java -jar ./target/cli-search-app-1.0.jar
```
you will see the following screen

Welcome to the data search application

to start searching please enter

1 for User search

2 for Organisation search

3 for Ticket search

or 'quit' anytime to exit the program

Enter your choice :

Then follow the instructions
