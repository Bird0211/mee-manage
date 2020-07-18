package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ProductBenefits {
    
    Integer Type;

    Integer SubsidyLimit;

    boolean IsUnlimited;

    Integer Quota;

    Integer QuotaRemaining;

}