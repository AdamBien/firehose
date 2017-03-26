/*
 */
package com.airhacks.firehose.configuration.boundary;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class ConfigurationStoreIT {

    private Client client;
    private WebTarget tut;

    @Before
    public void init() {
        this.client = ClientBuilder.newClient();
        this.tut = this.client.target("http://localhost:8080/firehose/resources/configurations");
    }

    @Test
    public void crud() {
        String configurationName = "micro-duke-" + System.currentTimeMillis();

        JsonObject origin = Json.createObjectBuilder().
                add("host", "duke:4242").
                build();

        Response initiallyCreated = this.tut.path(configurationName).
                request(MediaType.APPLICATION_JSON).
                put(Entity.json(origin));
        assertThat(initiallyCreated.getStatus(), is(201));

        String location = initiallyCreated.getHeaderString("Location");
        assertNotNull(location);
        System.out.println("location = " + location);

        Response evenCreated = this.client.target(location).
                request(MediaType.APPLICATION_JSON).
                get();
        assertThat(evenCreated.getStatus(), is(200));

        JsonObject existingConfiguration = evenCreated.readEntity(JsonObject.class);
        assertThat(origin.getString("host"), is(existingConfiguration.getString("host")));

        JsonObject updated = Json.createObjectBuilder().
                add("host", "nuke:4242").
                build();

        Response updatedResponse = this.tut.path(configurationName).
                request(MediaType.APPLICATION_JSON).
                put(Entity.json(updated));
        assertThat(updatedResponse.getStatus(), is(204));

        Response evenUpdated = this.tut.path(configurationName).
                request(MediaType.APPLICATION_JSON).
                get();
        assertThat(evenUpdated.getStatus(), is(200));


        Response deleteResponse = this.tut.path(configurationName).request().delete();
        assertThat(deleteResponse.getStatus(), is(204));

        Response evenDeleted = this.tut.path(configurationName).
                request(MediaType.APPLICATION_JSON).
                get();
        assertThat(evenDeleted.getStatus(), is(204));
    }

    @Test
    public void configureSampleService() {
        String configurationName = "sample-service";

        JsonObject origin = Json.createObjectBuilder().
                add("uri", "http://sample-service:8080/sample-service/resources/metrics").
                build();
        System.out.println("origin = " + origin);
        Response createdOrUpdated = this.tut.path(configurationName).
                request(MediaType.APPLICATION_JSON).
                put(Entity.json(origin));
        System.out.println("-- Status: " + createdOrUpdated.getStatus());
        assertThat(createdOrUpdated.getStatusInfo().getFamily(), is(Response.Status.Family.SUCCESSFUL));
    }

}
