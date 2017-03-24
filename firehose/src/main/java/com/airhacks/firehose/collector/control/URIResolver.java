
package com.airhacks.firehose.collector.control;

import java.util.Optional;

/**
 *
 * @author airhacks.com
 */
public class URIResolver {

    public static Optional<String> getHost(String metricName) {
        return Optional.ofNullable(System.getenv().get(metricName));
    }


}
