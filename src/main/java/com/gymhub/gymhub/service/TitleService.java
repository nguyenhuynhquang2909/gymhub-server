package com.gymhub.gymhub.service;

import com.gymhub.gymhub.dto.TitleEnum;
import org.springframework.stereotype.Service;

@Service
public class TitleService {

    public TitleEnum setTitleBasedOnLikeCounts(int likeCount) {
        if (likeCount <= 5) {
            return TitleEnum.CHICKEN_LEGS;
        } else if (likeCount <= 10) {
            return TitleEnum.GYM_RAT;
        } else if (likeCount <= 20) {
            return TitleEnum.JOHN_CENA;
        } else {
            return TitleEnum.MR_OLYMPIA;
        }
    }

    //call this whenever a member is liked
}
