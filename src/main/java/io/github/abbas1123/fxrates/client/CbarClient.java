package io.github.abbas1123.fxrates.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.abbas1123.fxrates.client.xml.ValCursXml;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Fetches the official daily feed from the Central Bank of Azerbaijan.
 * Wrapped in Resilience4j retry + circuit breaker; the service layer falls
 * back to the last successful snapshot when this fails.
 */
@Component
public class CbarClient implements RatesFeed {

    private static final DateTimeFormatter URL_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final RestClient restClient;
    private final XmlMapper xmlMapper;

    public CbarClient(RestClient.Builder restClientBuilder,
                      @Value("${cbar.base-url:https://www.cbar.az}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    @Retry(name = "cbar")
    @CircuitBreaker(name = "cbar")
    public ValCursXml fetch(LocalDate date) {
        byte[] body = restClient.get()
                .uri("/currencies/{date}.xml", date.format(URL_DATE))
                .retrieve()
                .body(byte[].class);

        try {
            return xmlMapper.readValue(body, ValCursXml.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to parse CBAR XML for " + date, e);
        }
    }
}
