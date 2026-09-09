package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.advice.GlobalExceptionHandler;
import com.pal.dipesh.razorpay.common.advice.GlobalResponseHandler;
import com.pal.dipesh.razorpay.common.context.CustomRequestContext;
import com.pal.dipesh.razorpay.common.context.CustomRequestContextFilter;
import com.pal.dipesh.razorpay.common.idempotency.IdempotencyFilter;
import com.pal.dipesh.razorpay.common.idempotency.IdempotencyStore;

import jakarta.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import tools.jackson.databind.json.JsonMapper;

/**
 * Servlet-web-only beans: request-scoped context bean, global exception
 * + response advice, and the idempotency filters.
 *
 * <p>Ordered after {@link CommonLibRedisAutoConfiguration} so that the optional
 * {@code IdempotencyStore} bean has been registered when we decide whether to
 * install {@link IdempotencyFilter}.
 *
 * <p>The response wrapper advice can be disabled by setting
 * {@code app.common.response-wrapper.enabled=false}, and the idempotency filter by setting
 * {@code app.common.idempotency.enabled=false} (the API gateway opts out this way: it proxies
 * rather than serves, so replay protection belongs on the downstream service).
 */
@AutoConfiguration(after = CommonLibRedisAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonLibWebAutoConfiguration {

    private static final String IDEMPOTENCY_PREFIX = "app.common.idempotency";
    private static final String REQUEST_CONTEXT_PREFIX = "app.common.request-context";

    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    @ConditionalOnMissingBean
    public CustomRequestContext customRequestContext() {
        return new CustomRequestContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<Filter> requestContextFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>(new RequestContextFilter());

        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "app.common.response-wrapper", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GlobalResponseHandler globalResponseHandler(JsonMapper jsonMapper) {
        return new GlobalResponseHandler(jsonMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = REQUEST_CONTEXT_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public CustomRequestContextFilter customRequestContextFilter(CustomRequestContext customRequestContext) {
        return new CustomRequestContextFilter(customRequestContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = REQUEST_CONTEXT_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CustomRequestContextFilter> customRequestContextFilterRegistration(CustomRequestContextFilter customRequestContextFilter) {
        FilterRegistrationBean<CustomRequestContextFilter> registration = new FilterRegistrationBean<>(customRequestContextFilter);

        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.addUrlPatterns("/*");

        return registration;
    }

    @Bean
    @ConditionalOnBean(IdempotencyStore.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = IDEMPOTENCY_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public IdempotencyFilter idempotencyFilter(CustomRequestContext customRequestContext,
                                               IdempotencyStore idempotencyStore,
                                               @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        return new IdempotencyFilter(idempotencyStore, customRequestContext, handlerExceptionResolver);
    }

    @Bean
    @ConditionalOnBean(IdempotencyStore.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = IDEMPOTENCY_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<IdempotencyFilter> idempotencyFilterRegistration(IdempotencyFilter idempotencyFilter) {
        FilterRegistrationBean<IdempotencyFilter> registration = new FilterRegistrationBean<>(idempotencyFilter);

        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        registration.addUrlPatterns("/*");

        return registration;
    }
}
