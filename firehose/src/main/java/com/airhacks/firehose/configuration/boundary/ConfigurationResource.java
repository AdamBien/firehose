
package com.airhacks.firehose.configuration.boundary;

import java.net.URI;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author airhacks.com
 */
@Stateless
@Path("configurations")
public class ConfigurationResource {

    @Inject
    ConfigurationStore configurationStore;

    @GET
    public JsonObject all() {
        return this.configurationStore.getAllConfigurations();
    }

    @GET
    public void getConfiguration(@Suspended AsyncResponse response, String configurationName) {
        this.configurationStore.
                getConfiguration(configurationName).
                ifPresent(response::resume);
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
                    path("/" + name).
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
