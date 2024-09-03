package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TitleEnumDeserializer extends JsonDeserializer<TitleEnum> {

    @Override
    public TitleEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText().toUpperCase().replace("-", "_").replace(".", "");
        return TitleEnum.valueOf(value);
    }
}
