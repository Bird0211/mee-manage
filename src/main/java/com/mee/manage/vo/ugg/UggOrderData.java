package com.mee.manage.vo.ugg;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class UggOrderData {

    String Guid;

    String SerialNum;

    String OrderGuid;

    String OrderSheetId;

    String Warehouse;

    String Channel;

    String ReciverName;

    String ReciverPhone;

    String ReciverTel;

    Integer ReciverCountryCode;

    String ReciverCountryName;

    String ReciverProvince;

    String ReciverCity;

    String ReciverDistrict;

    String ReciverPostCode;

    String ReciverAddr;

    String SenderName;

    String SenderPhone;

    String ProductId;

    String ProductNo;

    String ProductName;

    String Brand;

    String ColorId;

    String ColorCode;

    String ColorName;

    String SizeId;

    String SizeName;

    String Barcode;

    Double Weight;

    Integer OrderQty;

    Integer AllocQty;

    Integer SendQty;

    BigDecimal BalancePrice;

    String BalanceWithCurrency;

    BigDecimal ProductAmount;

    String ProductAmountWithCurrency;

    Integer TrafficCost;

    String TrafficCostWithCurrency;

    BigDecimal PayAmount;

    String PayTime;

    String PayAmountWithCurrency;

    Integer ProcessStatus;

    Integer PayStatus;

    Integer PayPlatform;

    Integer TrafficType;

    String TrafficTypeName;

    String TrafficCompany;

    String TrafficCompanyCode;

    String TrafficNo;

    Boolean IsLock;

    Boolean IsReturn;

    String SendDate;

    String CreateDate;

    String CreateTime;

    String Creator;

    String LastModTime;

    String LastModBy;

    String Remark;

    Boolean EnabledSmsNotice;

    String WayBillUrl;

    String DfCategory;
    
    String ClientOrderNo;

    Boolean IsAgentOrder;



    
}
