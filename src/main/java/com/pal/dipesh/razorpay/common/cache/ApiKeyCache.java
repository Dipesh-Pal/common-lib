package com.pal.dipesh.razorpay.common.cache;

import java.util.Optional;

public interface ApiKeyCache {
    Optional<ApiKeyCacheEntry> get(String keyId);

    void put(String keyId, ApiKeyCacheEntry entry);

    void evict(String keyId);
}
