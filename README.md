# FX Rates Service

![CI](https://github.com/abbas1123/fx-rates-service/actions/workflows/ci.yml/badge.svg)

REST API serving the **official Central Bank of Azerbaijan (CBAR) exchange rates** with sensible
production behaviors: Caffeine caching, Resilience4j retry + circuit breaker, stale-rate fallback
and a scheduled morning refresh.

Zero infrastructure — no database, no broker. Clone and run.

```bash
mvn spring-boot:run     # starts on :8082
```

## API

```bash
# Latest bulletin (all currencies + bank metals)
curl http://localhost:8082/api/rates

# One currency
curl http://localhost:8082/api/rates/USD
# {"code":"USD","name":"1 ABŞ dolları","nominal":1,"value":1.7000,"perUnit":1.700000}

# Conversion via AZN cross-rate
curl "http://localhost:8082/api/convert?from=USD&to=EUR&amount=100"
# {"from":"USD","to":"EUR","amount":100,"rate":0.858586,"result":85.86,"rateDate":"2026-07-03"}
```

Swagger UI: http://localhost:8082/swagger-ui.html

## How it behaves when CBAR is down

```
request ──► Caffeine cache (30 min TTL)
                │ miss
                ▼
        CbarClient.fetch()  ◄─ Resilience4j: 3 retries, circuit breaker
                │ fail
                ▼
        last successful snapshot (stale-but-served)
                │ none
                ▼
        503 + RFC 7807 problem body
```

For an FX display use case, *slightly stale* beats *unavailable* — the fallback chain encodes that
decision explicitly.

## Details worth reading

- **Nominal normalization** — CBAR quotes some currencies per 100 units (e.g. JPY). `Rate.perUnit`
  normalizes everything to per-1-unit so conversion math stays uniform.
- **Cross-rate conversion** — every pair is converted through AZN:
  `result = amount × perUnit(from) / perUnit(to)`, rate at 6 dp, money at 2 dp `HALF_UP`.
- **Scheduled warm-up** — CBAR publishes each morning; a `@Scheduled` job (09:15 Asia/Baku) evicts
  and re-warms the cache so the first user of the day gets a hot response.
- **Feed parsing is contract-tested** against a real bulletin sample (`src/test/resources/cbar-sample.xml`),
  including the nominal=100 case and Azerbaijani names in UTF-8.
- The upstream client sits behind a `RatesFeed` interface, so services are unit-testable without network.

## Stack

Java 17 · Spring Boot 3 · RestClient · jackson-dataformat-xml · Caffeine · Resilience4j · springdoc · JUnit 5 / Mockito / MockMvc
