package com.gymhub.gymhub.in_memory;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BanInfo {
    private Date banUntilDate;
    private String banReason;

    public BanInfo(Date banUntilDate, String banReason) {
        this.banUntilDate = banUntilDate;
        this.banReason = banReason;
    }
}
