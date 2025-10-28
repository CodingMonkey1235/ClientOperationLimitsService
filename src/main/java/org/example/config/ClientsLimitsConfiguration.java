package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "limits-settings")
public class ClientsLimitsConfiguration {
    BigDecimal defaultClientLimit;
    int cancelLimitDelay;
    int pageSize;
}
