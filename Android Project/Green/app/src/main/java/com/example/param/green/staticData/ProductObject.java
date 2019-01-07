package com.example.param.green.staticData;

/**
 * Created by Param on 25-08-2017.
 */

public class ProductObject {

    private String url,productName,description,expiryDate,discountPrecentage;
    private int mrpPrice,discountPrice;

    public String getUrl() {
        return url;
    }

    public String getProductName() {
        return productName;
    }

    public int getMrpPrice() {
        return mrpPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public String getDescription() {
        return description;
    }

    public String getDiscountPrecentage() {
        return discountPrecentage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscountPrecentage(String discountPrecentage) {
        this.discountPrecentage = discountPrecentage;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setMrpPrice(int mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}

