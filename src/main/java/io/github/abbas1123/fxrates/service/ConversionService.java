package io.github.abbas1123.fxrates.service;

import io.github.abbas1123.fxrates.dto.ConversionResponse;
import io.github.abbas1123.fxrates.model.Rate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConversionService {

    private final RatesService ratesService;

    public ConversionService(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    /**
     * Cross-rate through AZN: result = amount * perUnit(from) / perUnit(to).
     */
    public ConversionResponse convert(String from, String to, BigDecimal amount) {
        Rate fromRate = ratesService.rate(from);
        Rate toRate = ratesService.rate(to);

        BigDecimal appliedRate = fromRate.perUnit().divide(toRate.perUnit(), 6, RoundingMode.HALF_UP);
        BigDecimal result = amount.multiply(appliedRate).setScale(2, RoundingMode.HALF_UP);

        return new ConversionResponse(
                fromRate.code(),
                toRate.code(),
                amount,
                appliedRate,
                result,
                ratesService.current().date());
    }
}
