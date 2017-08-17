package com.cocosoft.ecart.cartmodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by.dmin on 3/20/2017.
 */

public class CartItem implements Parcelable {
    private String productId="";
    private String productName="";
    private String productDesc="";
    private Double productPrice=0.0;
    private String imageUrl="";
    private int count=0;
    private int scantype=0;
    private boolean isChecked=false;

    public CartItem(String productId, String productName, String productDesc, Double productPrice, int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
    }

    public CartItem(String productId, String productName, String productDesc, Double productPrice, String imageUrl, int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
    }

    protected CartItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productDesc = in.readString();
        productPrice = in.readDouble();
        imageUrl = in.readString();
        count = in.readInt();
        scantype = in.readInt();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CartItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public int getScantype() {
        return scantype;
    }

    public void setScantype(int scantype) {
        this.scantype = scantype;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productDesc);
        dest.writeDouble(productPrice);
        dest.writeString(imageUrl);
        dest.writeInt(count);
        dest.writeInt(scantype);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
