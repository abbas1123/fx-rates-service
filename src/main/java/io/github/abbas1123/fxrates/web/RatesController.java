package io.github.abbas1123.fxrates.web;

import io.github.abbas1123.fxrates.dto.ConversionResponse;
import io.github.abbas1123.fxrates.model.Rate;
import io.github.abbas1123.fxrates.model.RatesSnapshot;
import io.github.abbas1123.fxrates.service.ConversionService;
import io.github.abbas1123.fxrates.service.RatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "FX Rates", description = "Official CBAR rates against AZN")
public class RatesController {

    private final RatesService ratesService;
    private final ConversionService conversionService;

    public RatesController(RatesService ratesService, ConversionService conversionService) {
        this.ratesService = ratesService;
        this.conversionService = conversionService;
    }

    @GetMapping("/rates")
    @Operation(summary = "All rates from the latest CBAR bulletin")
    public RatesSnapshot allRates() {
        return ratesService.current();
    }

    @GetMapping("/rates/{code}")
    @Operation(summary = "Single currency rate, e.g. USD")
    public Rate rate(@PathVariable String code) {
        return ratesService.rate(code);
    }

    @GetMapping("/convert")
    @Operation(summary = "Convert between currencies via the AZN cross-rate")
    public ConversionResponse convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DecimalMin(value = "0.01") BigDecimal amount) {
        return conversionService.convert(from, to, amount);
    }
}
