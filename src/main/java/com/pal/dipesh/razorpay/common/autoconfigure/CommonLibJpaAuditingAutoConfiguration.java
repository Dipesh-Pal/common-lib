package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.audit.AuditorAwareImpl;
import com.pal.dipesh.razorpay.common.context.CustomRequestContext;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Registers an {@link AuditorAware} named {@code auditorAwareImpl} that reads the
 * current username (or merchant key) from the request-scoped contexts. Consumers
 * wire it in with {@code @EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")}.
 *
 * <p>Ordered after {@link CommonLibWebAutoConfiguration} because the context bean
 * is defined there.
 */
@ConditionalOnClass({AuditorAware.class, EnableJpaAuditing.class})
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@AutoConfiguration(after = CommonLibWebAutoConfiguration.class)
@ConditionalOnBean(CustomRequestContext.class)
public class CommonLibJpaAuditingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> auditorAwareImpl(CustomRequestContext customRequestContext) {
        return new AuditorAwareImpl(customRequestContext);
    }
}
