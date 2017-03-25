
package com.airhacks.firehose.configuration.boundary;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author airhacks.com
 */
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("configurations")
public class ConfigurationResource {

    @Inject
    ConfigurationStore configurationStore;

    @GET
    public JsonObject all() {
        return this.configurationStore.getAllConfigurations();
    }

    @GET
    @Path("{name}")
    public void getConfiguration(@Suspended AsyncResponse response, @PathParam("name") String configurationName) {
        response.setTimeout(1, TimeUnit.SECONDS);
        Optional<JsonObject> configuration = this.configurationStore.
                getConfiguration(configurationName);
        if (configuration.isPresent()) {
            response.resume(configuration.get());
        } else {
            response.resume(Response.noContent().build());
        }
    }

    @PUT
    @Path("{name}")
    public Response save(@PathParam("name") String name, JsonObject configuration, @Context UriInfo info) {
        boolean updated = this.configurationStore.
                saveOrUpdate(name, configuration);
        if (updated) {
            return Response.
                    noContent().
                    build();
        } else {
            URI uri = info.getAbsolutePathBuilder().
                    build();
            return Response.
                    created(uri).
                    build();
        }
    }

    @DELETE
    @Path("{name}")
    public void delete(@PathParam("name") String name) {
        this.configurationStore.remove(name);
    }

}
