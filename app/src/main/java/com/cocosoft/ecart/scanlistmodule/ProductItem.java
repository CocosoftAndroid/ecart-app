package com.cocosoft.ecart.scanlistmodule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by.dmin on 3/20/2017.
 */

public class ProductItem implements Parcelable {
    private String productId="";
    private String productName="";
    private double productPrice=0;
    private String productDesc="";
    private String imageUrl="";
    private int count=0;
    private int scantype=0;
    private boolean isChecked=false;

    public ProductItem(String productId, String productName, String productDesc,double productPrice, int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
        this.productDesc=productDesc;
    }

    public ProductItem(String productId, String productName, String productDesc,double productPrice, String imgurl,int count, int scantype, boolean isChecked) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.count = count;
        this.scantype = scantype;
        this.isChecked = isChecked;
        this.productDesc=productDesc;
        this.imageUrl=imgurl;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    protected ProductItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productDesc = in.readString();
        imageUrl= in.readString();
        productPrice = in.readDouble();
        count = in.readInt();
        scantype = in.readInt();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productDesc);
        dest.writeString(imageUrl);
        dest.writeDouble(productPrice);
        dest.writeInt(count);
        dest.writeInt(scantype);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ProductItem() {
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

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public static Creator<ProductItem> getCREATOR() {
        return CREATOR;
    }
}
