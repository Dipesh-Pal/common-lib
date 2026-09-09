package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.config.CookieProperties;
import com.pal.dipesh.razorpay.common.util.CookieUtil;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Exposes {@link CookieUtil} for the services that issue the refresh-token cookie (the API gateway
 * clears it, merchant-service sets it). Only activates when {@code app.security.cookie.path} is
 * present, so services that never touch cookies are not forced to carry cookie configuration.
 *
 * <p>Kept separate from {@link CommonLibWebAutoConfiguration} because
 * {@link EnableConfigurationProperties} binds eagerly: leaving it there would validate
 * {@link CookieProperties} in every servlet service and fail startup on the {@code @NotBlank}
 * {@code path} for those that legitimately have no cookie config.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "app.security.cookie", name = "path")
@EnableConfigurationProperties(CookieProperties.class)
public class CommonLibCookieAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CookieUtil cookieUtil(CookieProperties cookieProperties) {
        return new CookieUtil(cookieProperties);
    }
}
