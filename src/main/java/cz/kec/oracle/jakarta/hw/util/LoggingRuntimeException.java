package cz.kec.oracle.jakarta.hw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoggingRuntimeException
 *
 * @author kec
 * @since 3.8.19
 */
public class LoggingRuntimeException extends RuntimeException {

    private Logger LOG = LoggerFactory.getLogger(LoggingRuntimeException.class);

    public LoggingRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
    }
}
