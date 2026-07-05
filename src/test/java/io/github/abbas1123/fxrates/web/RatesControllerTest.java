package io.github.abbas1123.fxrates.web;

import io.github.abbas1123.fxrates.dto.ConversionResponse;
import io.github.abbas1123.fxrates.exception.RateNotFoundException;
import io.github.abbas1123.fxrates.model.Rate;
import io.github.abbas1123.fxrates.service.ConversionService;
import io.github.abbas1123.fxrates.service.RatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RatesControllerTest {

    @Mock
    private RatesService ratesService;

    @Mock
    private ConversionService conversionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RatesController(ratesService, conversionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsSingleRate() throws Exception {
        when(ratesService.rate("USD")).thenReturn(
                new Rate("USD", "1 ABŞ dolları", BigDecimal.ONE,
                        new BigDecimal("1.7000"), new BigDecimal("1.700000")));

        mockMvc.perform(get("/api/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("USD"))
                .andExpect(jsonPath("$.perUnit").value(1.7));
    }

    @Test
    void unknownCurrencyIs404() throws Exception {
        when(ratesService.rate("XXX")).thenThrow(new RateNotFoundException("XXX"));

        mockMvc.perform(get("/api/rates/XXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Rate not found"));
    }

    @Test
    void convertsViaQueryParams() throws Exception {
        when(conversionService.convert(eq("USD"), eq("AZN"), any(BigDecimal.class)))
                .thenReturn(new ConversionResponse("USD", "AZN", new BigDecimal("100"),
                        new BigDecimal("1.70"), new BigDecimal("170.00"), LocalDate.of(2026, 7, 3)));

        mockMvc.perform(get("/api/convert")
                        .param("from", "USD")
                        .param("to", "AZN")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(170.00));
    }
}
