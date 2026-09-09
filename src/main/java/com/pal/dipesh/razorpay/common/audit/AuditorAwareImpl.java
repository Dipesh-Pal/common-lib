package com.pal.dipesh.razorpay.common.audit;

import com.pal.dipesh.razorpay.common.context.CustomRequestContext;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM_AUDITOR = "system";

    private final CustomRequestContext customRequestContext;

    @Override
    public Optional<String> getCurrentAuditor() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return Optional.of(SYSTEM_AUDITOR);
        }

        if (customRequestContext.getUsername() != null) {
            return Optional.of(customRequestContext.getUsername());
        } else if (customRequestContext.getKeyId() != null) {
            return Optional.of(customRequestContext.getKeyId());
        }

        return Optional.of(SYSTEM_AUDITOR);
    }
}
