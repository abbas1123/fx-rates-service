package io.github.abbas1123.fxrates.service;

import io.github.abbas1123.fxrates.client.RatesFeed;
import io.github.abbas1123.fxrates.exception.RateNotFoundException;
import io.github.abbas1123.fxrates.exception.RatesUnavailableException;
import io.github.abbas1123.fxrates.model.Rate;
import io.github.abbas1123.fxrates.model.RatesSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RatesService {

    private static final Logger log = LoggerFactory.getLogger(RatesService.class);

    private final RatesFeed ratesFeed;
    private final Clock clock;
    private final AtomicReference<RatesSnapshot> lastKnown = new AtomicReference<>();

    public RatesService(RatesFeed ratesFeed, Clock clock) {
        this.ratesFeed = ratesFeed;
        this.clock = clock;
    }

    /**
     * Today's snapshot, served from Caffeine when warm. On upstream failure the
     * last successful snapshot is returned instead of an error — for FX display
     * purposes slightly stale beats unavailable.
     */
    @Cacheable(cacheNames = "rates", key = "'latest'")
    public RatesSnapshot current() {
        LocalDate today = LocalDate.now(clock);
        try {
            RatesSnapshot snapshot = RatesSnapshot.from(ratesFeed.fetch(today));
            lastKnown.set(snapshot);
            return snapshot;
        } catch (Exception ex) {
            RatesSnapshot stale = lastKnown.get();
            if (stale != null) {
                log.warn("CBAR fetch failed, serving last known rates from {}", stale.date(), ex);
                return stale;
            }
            throw new RatesUnavailableException("CBAR is unreachable and no cached rates are available", ex);
        }
    }

    public Rate rate(String code) {
        String normalized = code.toUpperCase(Locale.ROOT);
        if (Rate.AZN.code().equals(normalized)) {
            return Rate.AZN;
        }
        Rate rate = current().rates().get(normalized);
        if (rate == null) {
            throw new RateNotFoundException(normalized);
        }
        return rate;
    }

    @CacheEvict(cacheNames = "rates", allEntries = true)
    public void evictCache() {
        log.info("Rates cache evicted");
    }
}
