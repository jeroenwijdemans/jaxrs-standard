package com.wijdemans.standard;

import io.swagger.annotations.Api;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(
        value = "health",
        description = "health",
        tags = {"health"}
)
@Produces(MediaType.APPLICATION_JSON)
@Path("api/v1")
public class HealthResource {

    @GET
    @Path("/health")
    @PermitAll
    public Response healthCheck() {
        return Response.ok().build();
    }

    // TODO add feature / dependency that can do runtime health check
}
