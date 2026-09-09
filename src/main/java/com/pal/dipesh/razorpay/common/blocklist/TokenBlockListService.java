package com.pal.dipesh.razorpay.common.blocklist;

import java.time.Instant;

/**
 * Blocklist of revoked JWT jtis, shared by every service that validates tokens (the API gateway
 * on each request, the issuing service on logout). Implementations are expected to expire entries
 * at the token's own expiry so the store is self-cleaning.
 *
 * <p>Implementations must be fail-closed: if the backing store is unreachable the error
 * propagates so callers never silently treat a revoked token as valid.
 */
public interface TokenBlockListService {

    /**
     * Blocklist a single jti until {@code expiresAt}. No-op if the token is
     * already expired (TTL &le; 0) or the jti is already blocklisted
     * (uses SET NX so the original TTL is preserved on replay).
     */
    void blockList(String jti, Instant expiresAt);

    /**
     * Atomically blocklist an access/refresh jti pair in a single Redis
     * round trip via a Lua script. Either side may be {@code null} to skip
     * (e.g. refresh flow when the caller did not present an access token).
     * Each side is subject to the same TTL &le; 0 and SET-NX rules as
     * {@link #blockList(String, Instant)}.
     *
     * @return number of keys actually written (0, 1, or 2).
     */
    long blockListPair(String accessJti, Instant accessExpiresAt, String refreshJti, Instant refreshExpiresAt);

    /**
     * @return true iff {@code jti} is present in the blocklist.
     */
    boolean isBlockListed(String jti);
}

