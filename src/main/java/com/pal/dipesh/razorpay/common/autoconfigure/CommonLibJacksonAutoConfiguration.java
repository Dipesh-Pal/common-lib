package com.pal.dipesh.razorpay.common.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Contributes platform-wide Jackson defaults by <em>decorating</em> the {@link JsonMapper}
 * that Spring Boot's Jackson 3 autoconfiguration builds, instead of replacing it. This composes
 * cleanly with:
 * <ul>
 *   <li>Spring Boot's {@code spring.jackson.*} configuration properties,</li>
 *   <li>other libraries' {@link JsonMapperBuilderCustomizer} beans, and</li>
 *   <li>consumer-defined customizers (higher {@link org.springframework.core.Ordered} precedence wins).</li>
 * </ul>
 *
 * <p>Applied defaults:
 * <ul>
 *   <li>Value- and content-level {@link JsonInclude.Include#NON_NULL} serialization inclusion.</li>
 *   <li>ISO-8601 date/time output ({@link DateTimeFeature#WRITE_DATES_AS_TIMESTAMPS} disabled).</li>
 *   <li>{@link SerializationFeature#FAIL_ON_EMPTY_BEANS} disabled &mdash; TODO tighten in prod.</li>
 *   <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} disabled &mdash; TODO tighten in prod.</li>
 *   <li>{@link MapperFeature#ACCEPT_CASE_INSENSITIVE_ENUMS} enabled.</li>
 * </ul>
 *
 * <p>Note: unlike Jackson 2, no {@code JavaTimeModule} is registered here &mdash; Jackson 3 ships
 * {@code java.time.*} support inside {@code jackson-databind} and enables it out of the box.
 *
 * <p>Consumers can still fully replace the {@code JsonMapper} by declaring their own
 * bean; in that case Spring Boot's Jackson auto-config steps aside and this customizer
 * is simply not applied.
 */
@AutoConfiguration
@ConditionalOnClass({JsonMapper.class, JsonMapperBuilderCustomizer.class})
public class CommonLibJacksonAutoConfiguration {

    @Bean
    public JsonMapperBuilderCustomizer commonLibJacksonCustomizer() {
        return builder -> builder
                .changeDefaultPropertyInclusion(inclusion -> inclusion
                        .withValueInclusion(JsonInclude.Include.NON_NULL)
                        .withContentInclusion(JsonInclude.Include.NON_NULL))
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }
}
