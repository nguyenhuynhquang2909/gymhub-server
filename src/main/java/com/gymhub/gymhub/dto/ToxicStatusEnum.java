package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ToxicStatusEnumDeserializer.class)
public enum ToxicStatusEnum {
    PENDING,
    TOXIC,
    NOT_TOXIC
}
