
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

    public static final String MANDATORY_KEY_PREFIX = "firehose.";

    public static Optional<String> getValue(String configuration, String key) {
        return Optional.ofNullable(System.getenv(configuration + "." + key));
    }

    public static Optional<JsonObject> getConfiguration(String configurationName) {

        return getConfiguration(e -> e.getKey().startsWith(configurationName + "."));
    }

    public static Optional<JsonObject> getAllConfigurations() {
        return getConfiguration(e -> true);
    }

    public static Optional<JsonObject> getConfiguration(Predicate<Map.Entry<String, String>> filter) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        System.getenv().
                entrySet().
                stream().
                filter(e -> e.getKey().startsWith(MANDATORY_KEY_PREFIX)).
                filter(filter).
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
        //0: firehose
        //1: key
        //2: value
        return twoSegments[2];
    }

}
