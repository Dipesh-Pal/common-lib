package com.pal.dipesh.razorpay.common.exception;

import lombok.Getter;

/**
 * A backing store or downstream service a request depends on is unreachable, so the request could
 * not be served but may succeed on retry. Maps to {@code 503} with a {@code Retry-After} header.
 *
 * <p>Callers translate infrastructure errors into this exception explicitly rather than letting the
 * advice key off Spring's {@code DataAccessException}: that hierarchy also covers caller-caused
 * faults such as {@code DataIntegrityViolationException}, which must not be reported as an outage.
 */
@Getter
public class DependencyUnavailableException extends RuntimeException {

    private static final long DEFAULT_RETRY_AFTER_SECONDS = 5L;

    private final String errorCode;
    private final long retryAfterSeconds;

    public DependencyUnavailableException(String errorCode, String message, Throwable cause) {
        this(errorCode, message, DEFAULT_RETRY_AFTER_SECONDS, cause);
    }

    public DependencyUnavailableException(String errorCode, String message, long retryAfterSeconds, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
