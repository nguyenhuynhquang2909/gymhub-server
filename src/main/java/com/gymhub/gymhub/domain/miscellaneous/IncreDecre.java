package com.gymhub.gymhub.domain.miscellaneous;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "Containing 2 possible actions - INCREMENT and DECREMENT")
public class IncreDecre {
    @ApiModelProperty(value = "An enum with 2 possible values - INCREMENT and DECREMENT")
    private Action action;
}
