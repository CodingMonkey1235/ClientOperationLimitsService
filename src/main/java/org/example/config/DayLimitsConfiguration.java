package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "limits-settings")
@Getter @Setter
public class DayLimitsConfiguration {
    private BigDecimal defaultClientLimit;
    private int cancelLimitDelay;
    private int pageSize;
}
