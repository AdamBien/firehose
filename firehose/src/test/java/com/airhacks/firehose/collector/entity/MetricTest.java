/*
 */
package com.airhacks.firehose.collector.entity;

import javax.json.Json;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class MetricTest {

    @Test
    public void serialization() {
        Metric metric = new Metric("payara", "tx", "commit", "success", "42");
        metric.addLabel("type", "local");
        metric.addLabel("state", "no_timeout");
        String expected = "payara_tx_commit_success{state=no_timeout,type=local} 42\n";
        String actual = metric.toMetric();
        assertThat(actual, is(expected));
    }

    @Test
    public void creationWithValueAsNumberAndString() {
        long value = System.currentTimeMillis();
        JsonObject numberInput = Json.createObjectBuilder()
                .add("application", "sample-service")
                .add("component", "MetricsResource")
                .add("units", "ms")
                .add("suffix", "startup")
                .add("value", value)
                .build();
        Metric stringMetric = new Metric(numberInput, numberInput);
        String expected = stringMetric.toMetric();

        JsonObject stringInput = Json.createObjectBuilder()
                .add("application", "sample-service")
                .add("component", "MetricsResource")
                .add("units", "ms")
                .add("suffix", "startup")
                .add("value", String.valueOf(value))
                .build();

        Metric numberMetric = new Metric(stringInput, stringInput);
        String actual = numberMetric.toMetric();
        assertThat(actual, is(expected));

    }


}
