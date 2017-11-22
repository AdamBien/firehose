
package com.airhacks.firehose.extractor.control;

import javax.ejb.ApplicationException;
import javax.script.ScriptException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@ApplicationException(rollback = false)
public class ScriptEvaluationException extends WebApplicationException {

    public ScriptEvaluationException(String script, ScriptException ex) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE).
                header("exception-message", ex.getMessage()).
                header("line", ex.getLineNumber()).
                header("column", ex.getColumnNumber()).
                header("script", script).
                build());
    }
    public ScriptEvaluationException(String problem, String script, String input, Throwable ex) {
        super(Response.status(Response.Status.NOT_ACCEPTABLE).
                header("problem", problem).
                header("exception-message", ex.getMessage()).
                header("input", input).
                header("script", script).
                build());
    }

}
