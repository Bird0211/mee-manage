package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class ProfileTaxLiabilities {
    Integer Type;

    String Country;

    String Name;

    Integer FlatRate;

    String Description;

    boolean IsSellRestrictionsEffective;

    boolean IsTaxEffective;
}