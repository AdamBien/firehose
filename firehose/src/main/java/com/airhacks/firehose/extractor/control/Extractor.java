
package com.airhacks.firehose.extractor.control;

import java.io.StringReader;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author airhacks.com
 */
public class Extractor {

    private ScriptEngine scriptEngine;

    public final static String EXTRACTOR_KEY = "extractor";

    @PostConstruct
    public void initEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = scriptEngineManager.getEngineByName("javascript");
    }

    public Optional<Function<String, JsonObject>> getExtractor(Optional<JsonObject> optionalConfiguration) {
        if (optionalConfiguration.isPresent()) {
            JsonObject configuration = optionalConfiguration.get();
            String scriptContent = configuration.getString(EXTRACTOR_KEY, null);
            if (scriptContent != null) {
                return Optional.of(extractor(scriptContent));
            }
        }
        return Optional.empty();
    }

    Function<String, JsonObject> extractor(String scriptContent) {
        try {
            this.scriptEngine.eval(scriptContent);
        } catch (ScriptException ex) {
            throw new ScriptEvaluationException("Script evaluation failed", ex);
        }
        Invocable invocable = (Invocable) this.scriptEngine;
        Function<String, String> nashorn = invocable.getInterface(Function.class);
        Function<String, JsonObject> withToJson = nashorn.andThen(this::fromString);

        return (input) -> {
            try {
                return withToJson.apply(input);
            } catch (Throwable t) {
                throw new ScriptEvaluationException("Script execution failed", scriptContent, input, t);
            }
        };

        //return
    }



    JsonObject fromString(String content) {
        try (JsonReader reader = Json.createReader(new StringReader(content))) {
            return reader.readObject();
        }
    }

}
