package com.gymhub.gymhub.dto;

import java.util.Date;

public class BannedMemberDTO {

    private Long id;
    private String userName;
    private Date bannedUntil;
    private String reason;

    public BannedMemberDTO(Long id, String userName, Date bannedUntil, String reason) {
        this.id = id;
        this.userName = userName;
        this.bannedUntil = bannedUntil;
        this.reason = reason;
    }
}
