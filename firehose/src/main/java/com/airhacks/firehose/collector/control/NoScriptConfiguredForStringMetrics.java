
package com.airhacks.firehose.collector.control;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@ApplicationException(rollback = true)
public class NoScriptConfiguredForStringMetrics extends WebApplicationException {

    public NoScriptConfiguredForStringMetrics(String metricsName) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE).
                header("reason", "No javascript conversion script provided for the metrics: " + metricsName).
                build());
    }

}
