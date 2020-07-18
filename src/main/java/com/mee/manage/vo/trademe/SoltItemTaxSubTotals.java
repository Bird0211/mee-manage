package com.mee.manage.vo.trademe;

import lombok.Data;

@Data
public class SoltItemTaxSubTotals {
    
    Integer Type;

    String Country;

    String Name;

    Integer SubTotal;

    Integer FlatRate;
}