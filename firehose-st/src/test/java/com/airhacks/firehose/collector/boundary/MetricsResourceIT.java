
package com.airhacks.firehose.collector.boundary;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Assume;
import static org.junit.Assume.assumeThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class MetricsResourceIT {

    private Client client;
    private WebTarget tut;

    @Before
    public void init() {
        this.client = ClientBuilder.newClient();
        this.tut = this.client.target("http://localhost:8080/firehose/resources/metrics");
        Response pingResponse;
        try {
            pingResponse = this.client.target("http://localhost:8282/sample-service/resources/metrics").
                    request().
                    get();
            assumeThat(pingResponse.getStatusInfo().getFamily(), is(Response.Status.Family.SUCCESSFUL));
        } catch (Exception ex) {
            Assume.assumeNoException(ex);
            return;
        }

        JsonObject result = pingResponse.readEntity(JsonObject.class);
        System.out.println("result = " + result);

    }

    @Test
    public void sampleServiceMetrics() {

        Response response = this.tut.path("sample-service").
                request().
                get();
        assertThat(response.getStatus(), is(200));
        String metric = response.readEntity(String.class);
        assertNotNull(metric);
        System.out.println("metric = " + metric);
    }

    @Test
    public void allMetrics() {
        Response response = this.tut.request().get();
        assertThat(response.getStatus(), is(200));
        String metric = response.readEntity(String.class);
        assertNotNull(metric);
        System.out.println("metric = " + metric);
    }


}
