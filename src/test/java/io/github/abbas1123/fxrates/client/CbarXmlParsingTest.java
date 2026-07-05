package io.github.abbas1123.fxrates.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.abbas1123.fxrates.client.xml.ValCursXml;
import io.github.abbas1123.fxrates.model.Rate;
import io.github.abbas1123.fxrates.model.RatesSnapshot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CbarXmlParsingTest {

    private final XmlMapper xmlMapper = new XmlMapper();

    CbarXmlParsingTest() {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void parsesOfficialFeedFormat() throws Exception {
        byte[] xml = getClass().getResourceAsStream("/cbar-sample.xml").readAllBytes();

        RatesSnapshot snapshot = RatesSnapshot.from(xmlMapper.readValue(xml, ValCursXml.class));

        assertThat(snapshot.date()).isEqualTo(LocalDate.of(2026, 7, 3));
        assertThat(snapshot.rates()).containsKeys("USD", "EUR", "JPY", "GBP", "XAU");

        Rate usd = snapshot.rates().get("USD");
        assertThat(usd.perUnit()).isEqualByComparingTo("1.7");
        assertThat(usd.name()).contains("ABŞ dolları");
    }

    @Test
    void normalizesNominalGreaterThanOne() throws Exception {
        byte[] xml = getClass().getResourceAsStream("/cbar-sample.xml").readAllBytes();

        RatesSnapshot snapshot = RatesSnapshot.from(xmlMapper.readValue(xml, ValCursXml.class));

        Rate jpy = snapshot.rates().get("JPY");
        assertThat(jpy.nominal()).isEqualByComparingTo("100");
        assertThat(jpy.value()).isEqualByComparingTo("1.15");
        assertThat(jpy.perUnit()).isEqualByComparingTo("0.0115");
    }
}
