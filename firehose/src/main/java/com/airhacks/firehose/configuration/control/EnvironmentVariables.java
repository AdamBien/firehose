
package com.airhacks.firehose.configuration.control;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
        return Optional.ofNullable(System.getenv(MANDATORY_KEY_PREFIX + configuration + "." + key));
    }

    public static Optional<JsonObject> getConfiguration(String configurationName) {

        return getConfiguration(System.getenv(), e -> e.getKey().contains("." + configurationName + "."));
    }

    static Optional<JsonObject> getConfiguration(Map<String, String> environment, String configurationName) {

        return getConfiguration(environment, e -> e.getKey().contains("." + configurationName + "."));
    }

    public static Optional<JsonObject> getAllConfigurations() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        configurationNames().stream().
                forEach(name -> builder.add(name, getConfiguration(name).get()));
        JsonObject configurations = builder.build();
        if (configurations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(configurations);
    }

    public static List<String> configurationNames() {
        return configurationNames(System.getenv().keySet());
    }

    static List<String> configurationNames(Set<String> keys) {
        return keys.
                stream().
                filter(key -> key.startsWith(MANDATORY_KEY_PREFIX)).
                map(key -> extractConfigurationName(key)).
                distinct().
                collect(Collectors.toList());
    }


    public static Optional<JsonObject> getConfiguration(Map<String, String> environmentEntries, Predicate<Map.Entry<String, String>> filter) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        environmentEntries.
                entrySet().
                stream().
                filter(e -> e.getKey().startsWith(MANDATORY_KEY_PREFIX)).
                filter(filter).
                forEach(e -> builder.add(extractKeyName(e.getKey()), e.getValue()));
        JsonObject retVal = builder.build();
        if (retVal.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(retVal);
        }
    }

    static String extractConfigurationName(String key) {
        return EnvironmentVariables.extract(key, 1);
    }

    static String extractKeyName(String key) {
        return EnvironmentVariables.extract(key, 2);
    }

    static String extract(String key, int slot) {
        if (key == null || !key.contains(".")) {
            return key;
        }
        String[] twoSegments = key.split("\\.");
        //0: firehose
        //1: configuration name
        //2: key name
        return twoSegments[slot];
    }

}
