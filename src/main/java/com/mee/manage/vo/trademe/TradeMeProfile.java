package com.mee.manage.vo.trademe;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class TradeMeProfile {
    Integer WatchListCount;

    Integer WonCount;

    Integer LostCount;

    Integer SellingCount;

    Integer SoldCount;

    Integer UnsoldCount;

    Integer PositiveFeedback;

    Integer NegativeFeedback;

    Integer TotalFeedback;

    Integer MemberId;

    String Nickname;

    String DateJoined;

    String DateAddressVerified;

    Boolean IsAddressVerified;

    BigDecimal Balance;

    BigDecimal PayNowBalance;

    String FirstName;

    String LastName;

    Integer FixedPriceOffers;

    String Email;

    Integer Gender;

    Boolean IsBusiness;

    String BusinessName;

    String ClosestLocality;

    String ClosestDistrict;

    Integer ClosestDistrictId;

    Boolean IsAuthenticated;

    Boolean IsPayNowAccepted;

    Integer FavouriteSearchCount;

    Integer FavouriteCategoryCount;

    Integer FavouriteSellerCount;

    Boolean AutoBillingEnabled;

    Boolean CanListClearanceItems;

    Boolean IsTopSeller;

    Boolean IsRegisteredWineSeller;

    Boolean IsPropertyAgent;

    Boolean IsRentalAgent;

    Boolean IsJobAgent;

    String CreditCardType;

    String CreditCardLastFourDigits;

    Integer CreditCardExpiryMonth;

    Integer CreditCardExpiryYear;

    Boolean RecentSearchesEnabled;

    Integer HighVolumeListingCount;

    Integer HighVolumeThreshold;

    Boolean CanRemoveBids;

    Integer ShoppingCartCount;

    Boolean IsInTrade;

    String LandlinePhoneNumber;

    String MobilePhoneNumber;

    String MemberToken;

    String TradevineAccountType;

    ProfileMembershipAddress MembershipAddress;

    ProfileBillingAddress BillingAddress;

    ProfileMemberProfile MemberProfile;

    ProfileDraftSummary DraftSummary;

    List<ProfileJobProfiles> JobProfiles;

    Integer[] AvailableExternalPaymentProviders;

    List<ProfileMemberTokens> MemberTokens;

    ProfileLoyaltySubscriptionDetails LoyaltySubscriptionDetails;

    List<ProfileTaxLiabilities> TaxLiabilities;
}