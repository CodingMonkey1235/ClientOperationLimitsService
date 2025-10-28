package org.example.dto.clientsLimitsDto;

import java.math.BigDecimal;

public record HoldClientLimitRequestDto(long clientId, BigDecimal holdAmount) {
}
