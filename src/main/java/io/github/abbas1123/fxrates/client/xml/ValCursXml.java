package io.github.abbas1123.fxrates.client.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Root element of the daily CBAR feed: https://www.cbar.az/currencies/dd.MM.yyyy.xml
 */
@JacksonXmlRootElement(localName = "ValCurs")
public class ValCursXml {

    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    public String date;

    @JacksonXmlProperty(isAttribute = true, localName = "Name")
    public String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ValType")
    public List<ValTypeXml> valTypes;
}
