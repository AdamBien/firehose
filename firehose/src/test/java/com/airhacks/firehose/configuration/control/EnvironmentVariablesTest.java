/*
 */
package com.airhacks.firehose.configuration.control;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class EnvironmentVariablesTest {

    @Test
    public void splitWithoutDot() {
        String stringWithoutDot = "duke";
        assertThat(EnvironmentVariables.skipPrefix(stringWithoutDot), is(stringWithoutDot));
        assertNull(EnvironmentVariables.skipPrefix(null));
    }

    @Test
    public void skipPrefix() {
        String uri = "uri";
        assertThat(EnvironmentVariables.skipPrefix("firehose.duke." + uri), is(uri));

    }


}
