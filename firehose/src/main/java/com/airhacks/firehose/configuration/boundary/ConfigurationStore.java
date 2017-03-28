
package com.airhacks.firehose.configuration.boundary;

import com.airhacks.firehose.configuration.control.EnvironmentVariables;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author airhacks.com
 */
@ApplicationScoped
public class ConfigurationStore {

    private ConcurrentHashMap<String, JsonObject> configurationStore;

    @PostConstruct
    public void init() {
        this.configurationStore = new ConcurrentHashMap<>();
    }

    /**
     *
     * @param name metrics / configuration name
     * @param configuration metrics configuration
     * @return true = update, false = save
     */
    public boolean saveOrUpdate(String name, JsonObject configuration) {
        return this.configurationStore.put(name, configuration) != null;
    }

    public Optional<String> getValue(String configuration, String key) {
        Optional<String> environment = EnvironmentVariables.getValue(configuration, key);
        if (environment.isPresent()) {
            return environment;
        }
        return getConfiguration(key).
                map(o -> o.getString(key, null));
    }

    public Optional<JsonObject> getConfiguration(String key) {
        Optional<JsonObject> environment = EnvironmentVariables.getConfiguration(key);
        if (environment.isPresent()) {
            return environment;
        }
        return Optional.ofNullable(this.configurationStore.get(key));

    }

    JsonObject getAllConfigurations() {
        JsonObjectBuilder retVal = Json.createObjectBuilder();
        Optional<JsonObject> environmentEntries = EnvironmentVariables.getAllConfigurations();
        if (environmentEntries.isPresent()) {
            JsonObject environment = environmentEntries.get();
            environment.keySet().stream().forEach(key -> retVal.add(key, environment.getString(key, null)));
        }
        this.configurationStore.entrySet().forEach(e -> retVal.add(e.getKey(), e.getValue()));
        return retVal.build();
    }

    public void remove(String name) {
        this.configurationStore.remove(name);
    }

}
