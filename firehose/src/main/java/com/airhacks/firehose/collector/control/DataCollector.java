
package com.airhacks.firehose.collector.control;

import com.airhacks.firehose.collector.entity.Metric;
import com.airhacks.firehose.configuration.boundary.ConfigurationStore;
import com.airhacks.firehose.extractor.control.NashornExtractor;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
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

    @Inject
    NashornExtractor extractor;

    static final String URI = "uri";

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<Metric> fetchRemoteMetrics(String metricsName) {
        Optional<JsonObject> optionalConfiguration = this.configurationStore.getConfiguration(metricsName);
        Optional<Function<String, JsonObject>> optionalExtractor = this.extractor.getExtractor(optionalConfiguration);

        JsonObject configuration = optionalConfiguration.orElseThrow(() -> new MetricNotConfiguredException(metricsName));
        String extractedUri = extractUri(configuration);
        Response response = client.target(extractedUri).
                request(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN).
                get();
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            return Optional.empty();
        }

        Metric normalizedMetric = null;

        if (response.getMediaType() == MediaType.APPLICATION_JSON_TYPE) {
            JsonObject metric = response.readEntity(JsonObject.class);
            normalizedMetric = optionalExtractor.
                    map(f -> new Metric(configuration, f.apply(this.toString(metric)))).
                    orElse(new Metric(configuration, metric));
        } else {
            String rawContent = response.readEntity(String.class);
            normalizedMetric = optionalExtractor.
                    map(f -> new Metric(configuration, f.apply(rawContent))).
                    orElseThrow(() -> new NoScriptConfiguredForStringMetrics(metricsName));
        }
        return Optional.of(normalizedMetric);

    }

    String toString(JsonObject input) {
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(input);
            stringWriter.flush();
        }
        return stringWriter.getBuffer().toString();
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
