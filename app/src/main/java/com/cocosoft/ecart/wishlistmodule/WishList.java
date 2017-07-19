package com.cocosoft.ecart.wishlistmodule;

/**
 * Created by cocoadmin on 7/8/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WishList {

    @SerializedName("productId")
    @Expose
    private int productId;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("favourite")
    @Expose
    private Boolean favourite;

    public WishList(int productId, String userEmail, String productName, Double price, Long created, Boolean favourite) {
        this.productId = productId;
        this.userEmail = userEmail;
        this.productName = productName;
        this.price = price;
        this.created = created;
        this.favourite = favourite;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

}