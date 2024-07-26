package com.gymhub.gymhub.domain.miscellaneous;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Containing 2 possible actions - INCREMENT and DECREMENT")
public class IncreDecre {
    @Schema(description = "An enum with 2 possible values - INCREMENT and DECREMENT")
    private Action action;
}
