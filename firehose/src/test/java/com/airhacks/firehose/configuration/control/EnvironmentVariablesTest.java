/*
 */
package com.airhacks.firehose.configuration.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class EnvironmentVariablesTest {

    @Test
    public void extractConfigurationName() {
        String configurationName = "duke";
        assertThat(EnvironmentVariables.extractConfigurationName("firehose." + configurationName + ".uri"), is(configurationName));
    }

    @Test
    public void extractKeyName() {
        String keyName = "uri";
        assertThat(EnvironmentVariables.extractKeyName("firehose.duke." + keyName), is(keyName));
    }

    @Test
    public void configurationNames() {
        Map<String, String> entries = new HashMap<>();
        entries.put("firehose.sampleservice.uri", "http://sample-service:8080/sample-service/resources/metrics");
        entries.put("another.sampleservice.uri", "http://another:8080/another/resources/metrics");
        entries.put("firehose.another.uri", "http://another:8080/another/resources/metrics");
        List<String> configurationNames = EnvironmentVariables.configurationNames(entries.keySet());
        assertThat(configurationNames.size(), is(2));
        assertThat(configurationNames, hasItems("sampleservice", "another"));

    }



}
