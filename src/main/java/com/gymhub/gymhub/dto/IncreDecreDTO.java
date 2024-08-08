package com.gymhub.gymhub.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Containing 2 possible actions - INCREMENT and DECREMENT")
public class IncreDecreDTO {
    @Schema(description = "An enum with 2 possible values - INCREMENT and DECREMENT")
    private ActionEnum actionDTO;

    @Schema(description = "The id of the user who performs the action")
    private Long userId;

    @Schema(description = "The id of the post affected by the action")
    private Long postId;
}
