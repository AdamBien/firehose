/*
 */
package com.airhacks.firehose.extractor.control;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import javax.json.JsonObject;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class ExtractorTest {

    Extractor cut;

    @Before
    public void init() {
        this.cut = new Extractor();
        this.cut.initEngine();
    }

    @Test
    public void noopConversion() {
        String expectedName = "duke";
        String input = "{\"name\":\"" + expectedName + "\"}";
        String jsExtractor = "function apply(i){ return i;}";
        Function<String, JsonObject> extractor = this.cut.extractor(jsExtractor);
        assertNotNull(extractor);
        JsonObject origin = extractor.apply(input);
        assertNotNull(origin);
        assertThat(origin.getString("name"), is(expectedName));
    }

    @Test
    public void extractGFMetrics() throws IOException {
        String scriptContent = load("glassfish_methodstatistic.js");
        String metric = load("methodstatistic.json");
        assertNotNull(scriptContent);
        Function<String, JsonObject> extractor = this.cut.extractor(scriptContent);
        JsonObject jsonMetric = extractor.apply(metric);
        assertNotNull(jsonMetric);
        System.out.println("jsonMetric = " + jsonMetric);
        int value = jsonMetric.getJsonNumber("value").intValue();
        assertThat(value, is(-1));
    }


    public String load(String name) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("target/test-classes", name));
        return new String(bytes, Charset.forName("UTF-8"));
    }

}
