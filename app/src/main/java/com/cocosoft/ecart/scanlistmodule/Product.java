package com.cocosoft.ecart.scanlistmodule;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product implements Parcelable {

    @SerializedName("productName")
    @Expose
    private String productName;

    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("productDesc")
    @Expose
    private String productDesc;
    @SerializedName("oldprice")
    @Expose
    private Double oldprice;
    @SerializedName("imgUrl")
    @Expose
    private String imgUrl;


    private int count=0;
    private int scantype=0;
    private boolean isChecked=false;

    protected Product(Parcel in) {
        productName = in.readString();
        productId = in.readString();
        productDesc = in.readString();
        imgUrl = in.readString();
        count = in.readInt();
        scantype = in.readInt();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Double getOldprice() {
        return oldprice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setOldprice(Double oldprice) {
        this.oldprice = oldprice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productId);
        dest.writeString(productDesc);
        dest.writeString(imgUrl);
        dest.writeInt(count);
        dest.writeInt(scantype);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}