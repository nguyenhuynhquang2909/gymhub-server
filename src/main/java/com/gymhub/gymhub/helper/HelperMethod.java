package com.gymhub.gymhub.helper;

import com.gymhub.gymhub.dto.ToxicStatusEnum;

import java.util.UUID;

public class HelperMethod {
    public static long generateUniqueIds(){
        UUID id = UUID.randomUUID();
        long hash = id.hashCode();
        return hash;
    }

    //helper method to convert between toxicStatus to booleanToxicStatusValue

    public static int convertStringToxicStatusToBooleanValue (ToxicStatusEnum toxicStatus){
        if (toxicStatus.equals(ToxicStatusEnum.NOT_TOXIC)) {
            return 1;
        }
        if (toxicStatus.equals(ToxicStatusEnum.TOXIC)) {
            return -1;
        }
        if (toxicStatus.equals(ToxicStatusEnum.PENDING)) {
            return 0;
        }
        else {
            throw new RuntimeException("Invalid toxic status: " + toxicStatus);
        }
    }

    public static ToxicStatusEnum convertBooleanToxicStatusToStringValue(int toxicStatusBooleanValue) {
        switch (toxicStatusBooleanValue) {
            case 1:
                return ToxicStatusEnum.NOT_TOXIC;
            case -1:
                return ToxicStatusEnum.TOXIC;
            case 0:
                return ToxicStatusEnum.PENDING;
            default:
                throw new RuntimeException("Invalid toxic status value: " + toxicStatusBooleanValue);
        }
    }

}
