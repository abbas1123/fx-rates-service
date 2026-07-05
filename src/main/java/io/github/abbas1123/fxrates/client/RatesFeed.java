package io.github.abbas1123.fxrates.client;

import io.github.abbas1123.fxrates.client.xml.ValCursXml;

import java.time.LocalDate;

public interface RatesFeed {

    ValCursXml fetch(LocalDate date);
}
