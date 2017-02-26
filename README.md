
# JAXRS standard library

provides opinionated standards for JAXRS project.



# Dependencies

Expected the jackson library on the classpath:

 - 
 
# Usage

## Build 

Include dependency: 

```
dependencies {
    compile "com.wijdemans:jaxrs-cqrs-kafka:1.0.0"
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

# REST calls

 - give example
 - link to example project