package com.callpneck.activity.registrationSecond.Model;

public class FoodShop {
    String discountAvailable, offerLabel, name;

    public FoodShop(String discountAvailable, String offerLabel, String name) {
        this.discountAvailable = discountAvailable;
        this.offerLabel = offerLabel;
        this.name = name;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getOfferLabel() {
        return offerLabel;
    }

    public void setOfferLabel(String offerLabel) {
        this.offerLabel = offerLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
