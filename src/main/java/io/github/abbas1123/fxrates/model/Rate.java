package io.github.abbas1123.fxrates.model;

import io.github.abbas1123.fxrates.client.xml.ValuteXml;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * One currency quoted against AZN.
 *
 * @param perUnit AZN value of ONE unit of the currency (CBAR quotes some
 *                currencies per 100 units — the nominal — so this normalizes it)
 */
public record Rate(String code, String name, BigDecimal nominal, BigDecimal value, BigDecimal perUnit) {

    public static final Rate AZN = new Rate("AZN", "Azərbaycan manatı",
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE);

    public static Rate from(ValuteXml valute) {
        BigDecimal nominal = valute.nominal == null || valute.nominal.signum() == 0
                ? BigDecimal.ONE
                : valute.nominal;
        BigDecimal perUnit = valute.value.divide(nominal, 6, RoundingMode.HALF_UP);
        return new Rate(valute.code, valute.name, nominal, valute.value, perUnit);
    }
}
