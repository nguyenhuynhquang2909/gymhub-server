package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ThreadCategoryEnumDeserializer extends JsonDeserializer<ThreadCategoryEnum> {

    @Override
    public ThreadCategoryEnum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().toUpperCase();
        return ThreadCategoryEnum.valueOf(value);
    }
}