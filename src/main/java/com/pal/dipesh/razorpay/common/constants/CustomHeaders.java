package com.pal.dipesh.razorpay.common.constants;

import java.util.Set;

/**
 * Every non-standard HTTP header the platform defines, in one place — the counterpart to Spring's
 * {@code org.springframework.http.HttpHeaders} for standard ones.
 *
 * <p>These are deliberately centralised in common-lib because most of them have a producer in one
 * module and a consumer in another (the gateway stamps identity headers that downstream services
 * read back). A typo on either side does not fail loudly; it silently yields a null value, so the
 * literal must exist exactly once.
 */
public final class CustomHeaders {

    /* --- Identity: stamped by the gateway after authentication, read by CustomRequestContextFilter --- */

    public static final String MERCHANT_ID = "X-Merchant-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLE = "X-User-Role";
    public static final String KEY_ID = "X-Key-Id";
    public static final String ENVIRONMENT = "X-Environment";

    /* --- Token metadata: gateway -> auth endpoints, for the refresh and logout flows --- */

    public static final String ACCESS_TOKEN_SUBJECT = "X-Access-Token-Subject";
    public static final String ACCESS_TOKEN_JTI = "X-Access-Token-JTI";
    public static final String ACCESS_TOKEN_EXPIRES_AT = "X-Access-Token-Expires-At";
    public static final String ACCESS_TOKEN_RTI = "X-Access-Token-RTI";

    public static final String REFRESH_TOKEN_SUBJECT = "X-Refresh-Token-Subject";
    public static final String REFRESH_TOKEN_JTI = "X-Refresh-Token-JTI";
    public static final String REFRESH_TOKEN_EXPIRES_AT = "X-Refresh-Token-Expires-At";

    /* --- Idempotency: supplied by the caller, consumed by IdempotencyFilter --- */

    public static final String IDEMPOTENCY_KEY = "X-Idempotency-Key";

    /* --- Rate limiting: written onto responses by the gateway and the rate-limit advice --- */

    public static final String RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    public static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    public static final String RATE_LIMIT_RESET = "X-RateLimit-Reset";

    /* --- Webhooks: signature on outbound deliveries to merchant endpoints --- */

    public static final String WEBHOOK_SIGNATURE = "X-Razorpay-Signature";

    /**
     * Headers that only the API gateway may set, because downstream services treat them as proven
     * identity. The gateway strips any inbound copy of these before proxying, so a caller cannot
     * present itself as another merchant or user.
     *
     * <p>Deliberately excludes {@link #IDEMPOTENCY_KEY} (the caller is supposed to supply that),
     * the {@code RATE_LIMIT_*} family (response-only) and {@link #WEBHOOK_SIGNATURE} (outbound).
     */
    public static final Set<String> GATEWAY_MANAGED = Set.of(
            MERCHANT_ID,
            USER_ID,
            USER_ROLE,
            KEY_ID,
            ENVIRONMENT,
            ACCESS_TOKEN_SUBJECT,
            ACCESS_TOKEN_JTI,
            ACCESS_TOKEN_EXPIRES_AT,
            ACCESS_TOKEN_RTI,
            REFRESH_TOKEN_SUBJECT,
            REFRESH_TOKEN_JTI,
            REFRESH_TOKEN_EXPIRES_AT
    );

    private CustomHeaders() {
    }
}
