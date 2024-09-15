package com.gymhub.gymhub.dto;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class UpdateMemberPreviewResponseDTO {

    private String email;
    private String userName;
    private int likeCount;
    private String password; //encoded password from the database
    private String bio;
    private String avatar;

    public UpdateMemberPreviewResponseDTO(String email, String userName, int likeCount, String password, String bio, String avatar) {
        this.email = email;
        this.userName = userName;
        this.likeCount = likeCount;
        this.password = password;
        this.bio = bio;
        this.avatar = avatar;
    }

    public UpdateMemberPreviewResponseDTO() {
    }
}
