
package com.airhacks.firehose.collector.control;

import com.airhacks.firehose.collector.entity.Metric;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@Stateless
public class DataCollector {

    private Client client;

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<Metric> fetchRemoteMetrics(String uri) {
        Response response = client.target(uri).
                request(MediaType.APPLICATION_JSON).
                get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            return Optional.empty();
        }
        JsonObject metricsAsJson = response.readEntity(JsonObject.class);
        return Optional.of(new Metric(metricsAsJson));
    }

}
