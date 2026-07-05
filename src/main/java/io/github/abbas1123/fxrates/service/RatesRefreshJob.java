package io.github.abbas1123.fxrates.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * CBAR publishes the daily bulletin in the morning — refresh shortly after,
 * so the first user request of the day never pays the fetch latency.
 */
@Component
public class RatesRefreshJob {

    private static final Logger log = LoggerFactory.getLogger(RatesRefreshJob.class);

    private final RatesService ratesService;

    public RatesRefreshJob(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @Scheduled(cron = "0 15 9 * * *", zone = "Asia/Baku")
    public void refreshDailyRates() {
        ratesService.evictCache();
        try {
            log.info("Warmed rates cache for {}", ratesService.current().date());
        } catch (Exception ex) {
            log.error("Scheduled rates refresh failed", ex);
        }
    }
}
