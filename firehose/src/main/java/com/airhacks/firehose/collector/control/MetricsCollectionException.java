
package com.airhacks.firehose.collector.control;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@ApplicationException(rollback = true)
public class MetricsCollectionException extends WebApplicationException {

    public MetricsCollectionException(String reason, String message) {
        super(Response.status(400).header("reason", reason).
                header("exception", message).
                build());
    }


}
