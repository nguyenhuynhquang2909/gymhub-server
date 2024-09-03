package com.gymhub.gymhub.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TitleEnumDeserializer.class)
public enum TitleEnum {
    CHICKEN_LEGS, // 0 to 5
    GYM_RAT,      // 6 to 10
    JOHN_CENA,    // 11 to 20
    MR_OLYMPIA;   // above 20
}
