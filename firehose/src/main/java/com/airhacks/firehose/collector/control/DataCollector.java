
package com.airhacks.firehose.collector.control;

import com.airhacks.firehose.collector.entity.Metric;
import com.airhacks.firehose.configuration.boundary.ConfigurationStore;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

    @Inject
    ConfigurationStore configurationStore;

    static final String URI = "uri";

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<Metric> fetchRemoteMetrics(String metricsName) {
        Optional<JsonObject> optionalConfiguration = this.configurationStore.getConfiguration(metricsName);
        JsonObject configuration = optionalConfiguration.orElseThrow(() -> new MetricNotConfiguredException(metricsName));
        String extractedUri = extractUri(configuration);
        Response response = client.target(extractedUri).
                request(MediaType.APPLICATION_JSON).
                get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            return Optional.empty();
        }
        JsonObject applicationData = response.readEntity(JsonObject.class);
        return Optional.of(new Metric(configuration, applicationData));
    }
    
    public List<Metric> fetchRemoteMetrics() {
        return this.configurationStore.getConfigurationNames().
                stream().
                map(this::fetchRemoteMetrics).
                filter(m -> m.isPresent()).
                map(o -> o.get()).
                collect(Collectors.toList());
    }

    public String extractUri(JsonObject configuration) {
        return configuration.getString(URI);
    }


}
