package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class SoltItemBuyer {
    Integer MemberId;

    String Nickname;

    String DateAddressVerified;

    String DateJoined;

    String Email;

    Integer UniqueNegative;

    Integer UniquePositive;

    Integer FeedbackCount;

    Boolean IsAddressVerified;
}