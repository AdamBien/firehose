
package com.airhacks.firehose.configuration.control;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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

    public static Optional<JsonObject> getConfiguration(String configurationName) {

        return getConfiguration(configurationName, e -> true);
    }

    public static Optional<JsonObject> getConfiguration(String configurationName, Predicate<Map.Entry<String, String>> filter) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        System.getenv().
                entrySet().
                stream().
                filter(e -> e.getKey().startsWith(configurationName + ".")).
                forEach(e -> builder.add(skipPrefix(e.getKey()), e.getValue()));
        JsonObject retVal = builder.build();
        if (retVal.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(retVal);
        }

    }

    static String skipPrefix(String key) {
        if (key == null || !key.contains(".")) {
            return key;
        }
        String[] twoSegments = key.split("\\.");
        return twoSegments[1];
    }

}
