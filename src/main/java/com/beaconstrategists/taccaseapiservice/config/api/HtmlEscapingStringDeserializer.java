package com.beaconstrategists.taccaseapiservice.config.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class HtmlEscapingStringDeserializer extends JsonDeserializer<String> {

    private final boolean escapeHtmlStrings;

    public HtmlEscapingStringDeserializer(boolean escapeHtmlStrings) {
        this.escapeHtmlStrings = escapeHtmlStrings;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        String originalString = jsonParser.getValueAsString();
        return (originalString != null && escapeHtmlStrings)
                ? HtmlUtils.htmlEscape(originalString)
                : originalString;
    }
}