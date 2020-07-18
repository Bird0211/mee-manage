package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class SubscriptionDetailsSubscription {
    Integer Id;
    
    Integer Status;

    String SubscribedAt;

    String ExpiresAt;

    SubscriptionProduct Product;
}