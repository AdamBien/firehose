/*
 */
package com.airhacks.firehose.extractor.control;

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

}
