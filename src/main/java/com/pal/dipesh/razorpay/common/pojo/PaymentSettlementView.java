package com.pal.dipesh.razorpay.common.pojo;

import java.util.UUID;

public record PaymentSettlementView(
        UUID paymentId,
        int amountUnits,
        int refundAmountUnits,
        String currency
) {
}
