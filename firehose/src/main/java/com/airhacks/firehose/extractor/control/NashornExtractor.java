
package com.airhacks.firehose.extractor.control;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author airhacks.com
 */
public class NashornExtractor {

    private ScriptEngine scriptEngine;

    @PostConstruct
    public void initEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = scriptEngineManager.getEngineByExtension("javascript");
    }

    public Optional<Function<String, JsonObject>> getExtractor(Optional<JsonObject> optionalConfiguration) {
        if (optionalConfiguration.isPresent()) {
            JsonObject configuration = optionalConfiguration.get();
            String scriptContent = configuration.getString("extractor", null);
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
            throw new IllegalArgumentException("Script evaluation failed", ex);
        }
        Invocable invocable = (Invocable) this.scriptEngine;
        return invocable.getInterface(Function.class);
    }

}
