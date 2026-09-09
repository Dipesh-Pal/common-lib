package com.pal.dipesh.razorpay.common.autoconfigure;

import com.pal.dipesh.razorpay.common.config.KafkaProperties;
import com.pal.dipesh.razorpay.common.util.SignerUtil;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Framework-agnostic beans that have no external classpath or web requirements:
 * the HMAC {@link SignerUtil} and the {@link KafkaProperties} record binding.
 * Always active.
 */
@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class)
public class CommonLibCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SignerUtil signerUtil() {
        return new SignerUtil();
    }
}
