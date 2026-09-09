package com.pal.dipesh.razorpay.common.context;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CustomRequestContext {
    private UUID merchantId;
    private String username;
    private String keyId;
}
