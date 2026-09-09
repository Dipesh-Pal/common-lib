package com.pal.dipesh.razorpay.common.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.pal.dipesh.razorpay.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        LocalDateTime gracePeriodExpiresAt,
        UUID merchantId,
        Environment environment,
        boolean enabled
) {
    /**
     * Derived, time-dependent state. Marked {@link JsonIgnore} so it is never persisted into the
     * cache payload: it would be stale the moment it is written, and deserialization would break
     * if {@code FAIL_ON_UNKNOWN_PROPERTIES} were ever enabled.
     */
    @JsonIgnore
    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
