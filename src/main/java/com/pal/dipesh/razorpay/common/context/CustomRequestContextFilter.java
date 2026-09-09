package com.pal.dipesh.razorpay.common.context;

import com.pal.dipesh.razorpay.common.constants.CustomHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Populates the request-scoped {@link CustomRequestContext} from the identity headers the API
 * gateway attaches once it has authenticated the caller (see {@code JwtAuthHandler} and
 * {@code ApiKeyAuthHandler}). Downstream controllers then read the merchant/user off the context
 * instead of reparsing headers.
 *
 * <p>Runs at {@code HIGHEST_PRECEDENCE + 1}: after Spring's {@code RequestContextFilter}, which
 * establishes the {@code RequestContextHolder} that request scope depends on, and before the
 * idempotency filter at {@code +2}, which scopes its key by {@code merchantId}.
 *
 * <p>Missing or malformed headers leave the corresponding field null rather than failing the
 * request: not every route is merchant-scoped, and an unparseable id is the gateway's problem to
 * report, not a 500 here.
 *
 * <p><strong>Trust boundary:</strong> these headers are only trustworthy because the gateway
 * overwrites them on authenticated routes. Downstream services must not be reachable directly from
 * outside the cluster, or a caller could supply them itself.
 */
@Slf4j
@RequiredArgsConstructor
public class CustomRequestContextFilter extends OncePerRequestFilter {

    private final CustomRequestContext customRequestContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        customRequestContext.setMerchantId(readMerchantId(request));
        customRequestContext.setUsername(readHeader(request, CustomHeaders.USER_ID));
        customRequestContext.setKeyId(readHeader(request, CustomHeaders.KEY_ID));

        filterChain.doFilter(request, response);
    }

    private UUID readMerchantId(HttpServletRequest request) {
        String value = readHeader(request, CustomHeaders.MERCHANT_ID);

        if (value == null) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException _) {
            log.warn("Ignoring malformed {} header on path={}", CustomHeaders.MERCHANT_ID, request.getRequestURI());
            return null;
        }
    }

    private static String readHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }
}
