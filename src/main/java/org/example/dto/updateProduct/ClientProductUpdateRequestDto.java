package org.example.dto.updateProduct;

import java.math.BigDecimal;

public record ClientProductUpdateRequestDto(long productId, BigDecimal newBalance) {}
