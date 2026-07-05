package io.github.abbas1123.fxrates.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    /**
     * CBAR publishes per Baku calendar day; injectable for tests.
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Baku"));
    }
}
