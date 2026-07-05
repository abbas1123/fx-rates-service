package io.github.abbas1123.fxrates.model;

import io.github.abbas1123.fxrates.client.xml.ValCursXml;
import io.github.abbas1123.fxrates.client.xml.ValTypeXml;
import io.github.abbas1123.fxrates.client.xml.ValuteXml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record RatesSnapshot(LocalDate date, Map<String, Rate> rates) {

    private static final DateTimeFormatter CBAR_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static RatesSnapshot from(ValCursXml xml) {
        Map<String, Rate> rates = new LinkedHashMap<>();
        List<ValTypeXml> valTypes = xml.valTypes == null ? List.of() : xml.valTypes;
        for (ValTypeXml valType : valTypes) {
            if (valType.valutes == null) {
                continue;
            }
            for (ValuteXml valute : valType.valutes) {
                if (valute.code != null && valute.value != null) {
                    rates.put(valute.code.toUpperCase(), Rate.from(valute));
                }
            }
        }
        return new RatesSnapshot(LocalDate.parse(xml.date, CBAR_DATE), Map.copyOf(rates));
    }
}
