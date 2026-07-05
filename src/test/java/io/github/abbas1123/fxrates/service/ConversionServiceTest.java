package io.github.abbas1123.fxrates.service;

import io.github.abbas1123.fxrates.dto.ConversionResponse;
import io.github.abbas1123.fxrates.model.Rate;
import io.github.abbas1123.fxrates.model.RatesSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {

    private static final LocalDate RATE_DATE = LocalDate.of(2026, 7, 3);

    @Mock
    private RatesService ratesService;

    private ConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new ConversionService(ratesService);
        when(ratesService.current()).thenReturn(new RatesSnapshot(RATE_DATE, Map.of()));
    }

    @Test
    void convertsThroughAznCrossRate() {
        when(ratesService.rate("USD")).thenReturn(rate("USD", "1.700000"));
        when(ratesService.rate("EUR")).thenReturn(rate("EUR", "1.980000"));

        ConversionResponse response = conversionService.convert("USD", "EUR", new BigDecimal("100"));

        assertThat(response.rate()).isEqualByComparingTo("0.858586");
        assertThat(response.result()).isEqualByComparingTo("85.86");
        assertThat(response.rateDate()).isEqualTo(RATE_DATE);
    }

    @Test
    void convertsToAzn() {
        when(ratesService.rate("USD")).thenReturn(rate("USD", "1.700000"));
        when(ratesService.rate("AZN")).thenReturn(Rate.AZN);

        ConversionResponse response = conversionService.convert("USD", "AZN", new BigDecimal("100"));

        assertThat(response.rate()).isEqualByComparingTo("1.70");
        assertThat(response.result()).isEqualByComparingTo("170.00");
    }

    @Test
    void handlesNominalNormalizedCurrencies() {
        // JPY is quoted per 100 units: 1.15 AZN / 100 JPY = 0.0115 per unit
        when(ratesService.rate("AZN")).thenReturn(Rate.AZN);
        when(ratesService.rate("JPY")).thenReturn(rate("JPY", "0.011500"));

        ConversionResponse response = conversionService.convert("AZN", "JPY", new BigDecimal("11.50"));

        assertThat(response.rate()).isEqualByComparingTo("86.956522");
        assertThat(response.result()).isEqualByComparingTo("1000.00");
    }

    private static Rate rate(String code, String perUnit) {
        return new Rate(code, code, BigDecimal.ONE, new BigDecimal(perUnit), new BigDecimal(perUnit));
    }
}
