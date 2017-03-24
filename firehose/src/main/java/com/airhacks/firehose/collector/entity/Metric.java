
package com.airhacks.firehose.collector.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonObject;

/**
 *
 * @author airhacks.com
 */
public class Metric {

    private final List<String> metricParts;
    private final Map<String, String> labels;
    private final String value;

    /**
     *
     * @param application aka namespace
     * @param component further describes a metric within an application
     * @param units e.g. seconds
     * @param unitDescriptionSuffix e.g. total
     */
    public Metric(String application, String component, String units, String unitDescriptionSuffix, String value) {
        this.labels = new HashMap<>();
        this.value = value;
        this.metricParts = Arrays.asList(application, component, units, unitDescriptionSuffix);
    }

    public Metric(JsonObject metricsAsJson) {
        this(metricsAsJson.getString("application", null),
                metricsAsJson.getString("component", null),
                metricsAsJson.getString("units", null),
                metricsAsJson.getString("suffix", null),
                metricsAsJson.getString("value")
        );
    }

    public void addLabel(String name, String value) {
        this.labels.put(name, value);
    }

    public String toMetric() {
        String metric = this.metricParts.stream().
                filter(s -> s != null).
                collect(Collectors.joining("_"));
        if (this.labels.isEmpty()) {
            return addValue(metric, this.value);
        }
        String metricWithLabels = metric + "{" + this.getLabels() + "}";
        return addValue(metricWithLabels, this.value);
    }

    String addValue(String metric, String value) {
        return metric + " " + value + "\n";
    }

    String getLabels() {
        return this.labels.entrySet().
                stream().
                map(entry -> entry.getKey() + "=" + entry.getValue()).
                collect(Collectors.joining(","));
    }


}
