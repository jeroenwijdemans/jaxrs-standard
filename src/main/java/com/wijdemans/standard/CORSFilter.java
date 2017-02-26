package com.wijdemans.standard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

// prio before security
@Priority(500)
@Provider
public class CORSFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        // TODO handle origin - set to whatever we agree upon
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");

        String httpMethod = requestContext.getMethod();
        if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
            logger.debug("Got preflight check.");
            cres.getHeaders().add("Access-Control-Allow-Headers", "haAO, origin, content-type, accept, authorization, X-Requested-With");
            cres.getHeaders().add("Access-Control-Request-Headers", "*");
            cres.setStatus(200);
            return;
        }
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, X-Requested-With");
    }
}