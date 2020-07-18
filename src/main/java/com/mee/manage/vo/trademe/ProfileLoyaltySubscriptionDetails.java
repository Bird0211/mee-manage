package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ProfileLoyaltySubscriptionDetails {
    boolean IsMemberEligibleForNewSubscription;

    SubscriptionDetailsSubscription Subscription;
}