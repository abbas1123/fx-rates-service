package io.github.abbas1123.fxrates.client.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class ValuteXml {

    @JacksonXmlProperty(isAttribute = true, localName = "Code")
    public String code;

    @JacksonXmlProperty(localName = "Nominal")
    public BigDecimal nominal;

    @JacksonXmlProperty(localName = "Name")
    public String name;

    @JacksonXmlProperty(localName = "Value")
    public BigDecimal value;
}
