
package com.airhacks.sample.boundary;

import java.util.concurrent.atomic.AtomicLong;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author airhacks.com
 */
@Path("metrics")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    private AtomicLong counter = new AtomicLong();

    @GET
    public JsonObject metric() {
        return Json.createObjectBuilder()
                .add("application", "sample-service")
                .add("component", "MetricsResource")
                .add("units", "request")
                .add("suffix", "total")
                .add("value", String.valueOf(counter.incrementAndGet()))
                .build();
    }

}
