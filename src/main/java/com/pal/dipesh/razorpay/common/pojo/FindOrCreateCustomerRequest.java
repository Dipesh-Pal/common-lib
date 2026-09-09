package com.pal.dipesh.razorpay.common.pojo;

import java.util.UUID;

public record FindOrCreateCustomerRequest(
        UUID merchantId,
        String email,
        String name,
        String phone
) {
}
