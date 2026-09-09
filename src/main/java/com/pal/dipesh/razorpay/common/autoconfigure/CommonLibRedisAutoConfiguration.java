package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.blocklist.RedisTokenBlockListService;
import com.pal.dipesh.razorpay.common.blocklist.TokenBlockListService;
import com.pal.dipesh.razorpay.common.idempotency.IdempotencyStore;
import com.pal.dipesh.razorpay.common.idempotency.RedisIdempotencyStore;
import com.pal.dipesh.razorpay.common.ratelimit.RateLimiter;
import com.pal.dipesh.razorpay.common.ratelimit.TokenBucketRateLimiter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Registers the Redis-backed shared infrastructure that needs nothing beyond a
 * {@link StringRedisTemplate}: the {@link IdempotencyStore}, the {@link RateLimiter}, and the
 * {@link TokenBlockListService}. Ordered after Spring Boot's own
 * {@link DataRedisAutoConfiguration} so the {@link RedisConnectionFactory} is guaranteed to exist
 * by the time we ask for it.
 *
 * <p>The Redis-backed API key cache lives in {@link CommonLibCacheAutoConfiguration} instead,
 * since it additionally requires Jackson and should not drag that requirement onto these beans.
 *
 * <p>Every bean backs off if the consumer declares its own, so services can substitute a
 * different implementation without excluding this auto-configuration.
 */
@AutoConfiguration(after = DataRedisAutoConfiguration.class)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnBean(RedisConnectionFactory.class)
public class CommonLibRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdempotencyStore.class)
    public IdempotencyStore redisIdempotencyStore(StringRedisTemplate redisTemplate) {
        return new RedisIdempotencyStore(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    public RateLimiter tokenBucketRateLimiter(StringRedisTemplate redisTemplate) {
        return new TokenBucketRateLimiter(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(TokenBlockListService.class)
    public TokenBlockListService redisTokenBlockListService(StringRedisTemplate redisTemplate) {
        return new RedisTokenBlockListService(redisTemplate);
    }
}
