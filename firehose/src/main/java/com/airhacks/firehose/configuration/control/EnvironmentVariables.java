
package com.airhacks.firehose.configuration.control;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author airhacks.com
 */
public class EnvironmentVariables {

    public static Optional<String> getValue(String configuration, String key) {
        return Optional.ofNullable(System.getenv(configuration + "." + key));
    }

    public static Optional<JsonObject> getConfiguration(String configuration) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        System.getenv().entrySet().
                stream().
                filter(e -> e.getKey().startsWith(configuration)).
                forEach(e -> builder.add(e.getKey(), e.getValue()));
        JsonObject retVal = builder.build();
        if (retVal.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(retVal);
        }

    }


}
