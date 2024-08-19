package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ThreadCategoryEnumDeserializer.class)
public enum ThreadCategoryEnum {
    FLEXING,
    ADVICE,
    SUPPLEMENT
}