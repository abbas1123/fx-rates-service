package io.github.abbas1123.fxrates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class FxRatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxRatesApplication.class, args);
    }
}
