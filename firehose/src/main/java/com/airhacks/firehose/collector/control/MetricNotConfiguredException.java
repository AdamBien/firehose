
package com.airhacks.firehose.collector.control;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@ApplicationException(rollback = true)
public class MetricNotConfiguredException extends WebApplicationException {

    public MetricNotConfiguredException(String metricName) {
        super("Configuration for " + metricName + " not found!", Response.Status.INTERNAL_SERVER_ERROR);
    }

}
