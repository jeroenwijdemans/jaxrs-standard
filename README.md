_Work in progress_

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5637f6ca681e4cf49b001f8e8d8de4b8)](https://www.codacy.com/app/jeroenwijdemans/jaxrs-standard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jeroenwijdemans/jaxrs-standard&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/jeroenwijdemans/jaxrs-standard.svg?branch=master)](https://travis-ci.org/jeroenwijdemans/jaxrs-standard)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![codecov](https://codecov.io/gh/jeroenwijdemans/jaxrs-standard/branch/master/graph/badge.svg)](https://codecov.io/gh/jeroenwijdemans/jaxrs-standard)

# JAXRS standard library

provides opinionated utility classes for JAXRS project.

Add the following: 

 - Config
 - CORS Filter 
 - Healthcheck
 - Immediate Feature
 - Security Filter
  
 
#### Config
Configuration is intended to read yaml, for example on Kubernetes

#### CORS Filter
Register domains for this service from Config to easily check CORS without using *

#### Health checks
Quick check to see if the JAXRS is up
(note that this does not need to imply your app is really in working order)

#### Immediate Feature
JAXRS feature that enable the usage of the @Immediate annotation.
This annotation makes sure that the class is eagerly initialized in HK2 

##### Security filter
Simple check that:
  
  - checks if security is needed (in Config)
  - reads a JWT token
  - Validates the token
 
# Dependencies

Expected the following on the classpath:

 - jaxrs implementation
 - javax inject implementation
 - javax annotation implementation
 - hk2-api implementation
 - swagger annotation implementation
 - jackson implementation
 
 JAXRS 2.0 is the reference implementation that works.

 [Microservice template](https://github.com/jeroenwijdemans/microservice-template) 
 is a reference implementation for, amongst others, this library.
 
# Usage

## Build 

Include dependency: 

```
dependencies {
    compile "com.wijdemans:jaxrs-standard:1.0.0"
}
``` 

## gradle run

To run a task from Gradle we need to add PROPERTIES_LOCATION to the environment.

```groovy

task(run, dependsOn: 'classes', type: JavaExec) {
    environment 'PROPERTIES_LOCATION', file('./properties/local').absolutePath
    main = 'com.wijdemans.Main'
    classpath = sourceSets.main.runtimeClasspath
}

```


Add to container:

```java

class Main {
    
    private ResourceConfig createResourceConfig () {
        
        final ResourceConfig rc = new ResourceConfig();
        rc.register(JaxRsErrorMapper.class);
        
        return rc;
    }  
        
}

```
