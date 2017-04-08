
package com.airhacks.firehose.collector.boundary;

import com.airhacks.firehose.collector.control.DataCollector;
import com.airhacks.firehose.collector.entity.Metric;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@Stateless
@Path("metrics")
@Produces(MediaType.TEXT_PLAIN)
public class MetricsResource {

    @Inject
    DataCollector dataCollector;

    @GET
    @Path("{name}")
    public void metrics(@Suspended AsyncResponse response, @PathParam("name") String name) {
        response.setTimeout(1, TimeUnit.SECONDS);
        Optional<String> metric = dataCollector.
                fetchRemoteMetrics(name).
                map(m -> m.toMetric());
        if (metric.isPresent()) {
            response.resume(metric.get());
        } else {
            response.resume(Response.noContent().build());
        }
    }

    @GET
    public void all(@Suspended AsyncResponse response) {
        response.setTimeout(1, TimeUnit.SECONDS);
        List<Metric> remoteMetrics = dataCollector.fetchRemoteMetrics();
        String metrics = remoteMetrics.stream().
                map(Metric::toMetric).
                collect(Collectors.joining());
        response.resume(metrics);
    }

}
