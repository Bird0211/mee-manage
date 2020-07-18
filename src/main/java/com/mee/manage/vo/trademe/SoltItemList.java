package com.mee.manage.vo.trademe;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SoltItemList {

    Long ListingId;

    String Title;

    String Category;

    BigDecimal StartPrice;

    BigDecimal BuyNowPrice;

    String StartDate;

    String EndDate;

    String ListingLength;

    String Restrictions;

    Boolean IsFeatured;

    Boolean HasGallery;

    Boolean IsBold;

    Boolean IsHighlighted;

    Boolean HasHomePageFeature;

    Boolean HasExtraPhotos;

    Boolean HasScheduledEndDate;

    Integer BidderAndWatchers;

    Integer MaxBidAmount;

    String AsAt;

    String CategoryPath;

    String PictureHref;

    Integer PhotoId;

    Integer BidCount;

    Integer ViewCount;

    Boolean IsReserveMet;

    Boolean HasReserve;

    Boolean HasBuyNow;

    String Note;

    Integer NoteId;
    
    String NoteDate;

    String CategoryName;

    Integer ReserveState;

    Boolean IsClassified;

    Long RelistedItemId;

    String Subtitle;

    BigDecimal ReservePrice;

    Boolean IsBuyNowOnly;

    Boolean IsFlatShippingCharge;

    String RemainingGalleryPlusRelists;

    String ExternalReferenceId;

    String SKU;

    Boolean IsClearance;

    BigDecimal WasPrice;

    Integer PercentageOff;

    Integer ListingGroupId;

    Integer Status;

    String StatusDate;

    String SoldDate;

    String SoldType;

    BigDecimal SalePrice;

    String SelectedShipping;

    String BuyerDeliveryAddress;

    String MessageFromBuyer;

    Integer SuccessFees;

    Boolean IsPayNowPurchase;

    Boolean IsPaymentPending;

    Integer PayNowRefundValue;

    Integer ShippingPrice;

    Integer ShippingType;

    Boolean HasSellerPlaceFeedback;

    Boolean HasBuyerPlaceFeedback;

    Integer OfferId;

    Integer InvoiceId;

    Integer QuantitySold;

    Integer PurchaseId;

    String ReferenceNumber;

    BigDecimal Price;

    BigDecimal SubtotalPrice;

    BigDecimal TotalShippingPrice;

    BigDecimal TotalSalePrice;

    String OrderId;

    Integer PaymentMethod;

    SoltItemBuyer Buyer;

    List<SoltItemAttributes> Attributes;

    SoltItemDeliveryAddress DeliveryAddress;

    SoltItemPaymentDetails PaymentDetails;

    List<SoltItemTrackedParcels> TrackedParcels;

    List<SoltItemTaxSubTotals> TaxSubTotals;
    
}