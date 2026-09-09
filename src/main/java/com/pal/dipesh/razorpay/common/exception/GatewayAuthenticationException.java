package com.pal.dipesh.razorpay.common.exception;

import lombok.Getter;

/**
 * Raised when authentication cannot be completed at the edge. Lives in common-lib rather than the
 * gateway module so the shared {@code GlobalExceptionHandler} can render it into the standard
 * {@code ApiResponse} envelope.
 */
@Getter
public class GatewayAuthenticationException extends RuntimeException {

    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String INVALID_TOKEN_CLAIMS = "INVALID_TOKEN_CLAIMS";

    private final String errorCode;

    public GatewayAuthenticationException(String message) {
        this(UNAUTHORIZED, message, null);
    }

    public GatewayAuthenticationException(String message, Throwable ex) {
        this(UNAUTHORIZED, message, ex);
    }

    public GatewayAuthenticationException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public GatewayAuthenticationException(String errorCode, String message, Throwable ex) {
        super(message, ex);
        this.errorCode = errorCode;
    }
}
