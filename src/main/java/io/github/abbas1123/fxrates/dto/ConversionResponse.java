package io.github.abbas1123.fxrates.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConversionResponse(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal result,
        LocalDate rateDate) {
}
