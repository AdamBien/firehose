
package com.airhacks.firehose.extractor.control;

import javax.json.JsonObject;

/**
 *
 * @author airhacks.com
 */
@FunctionalInterface
public interface Extractor {
    JsonObject extractMetrics(String anyformat);
}
