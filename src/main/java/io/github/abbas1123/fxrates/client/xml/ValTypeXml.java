package io.github.abbas1123.fxrates.client.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class ValTypeXml {

    @JacksonXmlProperty(isAttribute = true, localName = "Type")
    public String type;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    public List<ValuteXml> valutes;
}
