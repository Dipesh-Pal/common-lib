package com.pal.dipesh.razorpay.common.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Exposes an AES-GCM {@link BytesEncryptor} named {@code masterKeyEncryptor} for
 * the vault module. Only activates when {@code app.vault.master-key} is present,
 * so services that don't need envelope encryption pay no penalty.
 *
 * <p>Kept separate from {@link CommonLibCoreAutoConfiguration} so the classpath and property
 * conditions stay at class level: {@link BytesEncryptor} is the bean method's return type, so
 * a method-level {@link ConditionalOnClass} would still require the type to be loadable.
 */
@AutoConfiguration
@ConditionalOnClass(BytesEncryptor.class)
@ConditionalOnProperty(prefix = "app.vault", name = "master-key")
public class CommonLibEncryptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "masterKeyEncryptor")
    public BytesEncryptor masterKeyEncryptor(@Value("${app.vault.master-key}") String masterKey) {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(masterKey), "AES/GCM/NoPadding");
        return new AesBytesEncryptor(secretKey, KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
