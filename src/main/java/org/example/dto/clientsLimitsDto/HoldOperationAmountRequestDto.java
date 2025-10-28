package org.example.dto.clientsLimitsDto;

import java.math.BigDecimal;

public record HoldOperationAmountRequestDto(long clientId, BigDecimal holdAmount) {
}
