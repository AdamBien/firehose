/*
 */
package com.airhacks.firehose.collector.entity;

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

}
