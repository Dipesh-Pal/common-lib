package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.cache.ApiKeyCache;
import com.pal.dipesh.razorpay.common.cache.ApiKeyCacheEntry;
import com.pal.dipesh.razorpay.common.cache.RedisApiKeyCache;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import tools.jackson.databind.json.JsonMapper;

/**
 * Registers the Redis-backed {@link ApiKeyCache} used to avoid a datastore round-trip on every
 * authenticated request. {@link ApiKeyCacheEntry} is a plain record and {@link ApiKeyCache} is the
 * abstraction, so only the {@link RedisApiKeyCache} implementation needs a bean definition.
 *
 * <p>Kept separate from {@link CommonLibRedisAutoConfiguration} because it is the only
 * Redis-backed bean that additionally requires Jackson: entries are stored as JSON strings
 * (readable in {@code redis-cli} and decoupled from JDK serialization). Isolating it here means
 * the {@link JsonMapper} requirement gates this bean alone and never the other Redis beans.
 *
 * <p>Ordered after {@link CommonLibRedisAutoConfiguration} and {@link JacksonAutoConfiguration} so
 * both collaborators — the {@link StringRedisTemplate} and the fully customized {@link JsonMapper}
 * (see {@link CommonLibJacksonAutoConfiguration}) — are in place before this runs.
 *
 * <p>Consumers can substitute their own implementation by declaring any {@link ApiKeyCache} bean.
 */
@AutoConfiguration(after = {CommonLibRedisAutoConfiguration.class, JacksonAutoConfiguration.class})
@ConditionalOnClass({StringRedisTemplate.class, JsonMapper.class})
@ConditionalOnBean(RedisConnectionFactory.class)
public class CommonLibCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ApiKeyCache.class)
    public ApiKeyCache redisApiKeyCache(StringRedisTemplate redisTemplate, JsonMapper jsonMapper) {
        return new RedisApiKeyCache(redisTemplate, jsonMapper);
    }
}
