package org.example.config;

import org.example.repository.DayLimitsRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DayLimitsConfiguration.class)
public class ApplicationConfiguration {

}
