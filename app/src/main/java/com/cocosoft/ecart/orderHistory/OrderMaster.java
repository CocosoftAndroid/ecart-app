package com.cocosoft.ecart.orderHistory;

/**
 * Created by cocoadmin on 7/15/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderMaster {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;
    @SerializedName("paymentType")
    @Expose
    private String paymentType;
    @SerializedName("shipmentAddress")
    @Expose
    private Object shipmentAddress;
    @SerializedName("shipmentType")
    @Expose
    private String shipmentType;
    @SerializedName("shipmentStatus")
    @Expose
    private String shipmentStatus;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("couponCode")
    @Expose
    private Object couponCode;
    @SerializedName("orderDate")
    @Expose
    private String orderDate;
    @SerializedName("totalItems")
    @Expose
    private Integer totalItems;
    @SerializedName("totalPrice")
    @Expose
    private Object totalPrice;
    @SerializedName("orderList")
    @Expose
    private List<OrderList> orderList = null;
    @SerializedName("created")
    @Expose
    private Long created;

    public OrderMaster(String transactionId, Integer id, String paymentStatus, String paymentType, Object shipmentAddress, String shipmentType, String shipmentStatus, String userEmail, Object couponCode, String orderDate, Integer totalItems, Object totalPrice, List<OrderList> orderList, Long created) {
        this.transactionId = transactionId;
        this.id = id;
        this.paymentStatus = paymentStatus;
        this.paymentType = paymentType;
        this.shipmentAddress = shipmentAddress;
        this.shipmentType = shipmentType;
        this.shipmentStatus = shipmentStatus;
        this.userEmail = userEmail;
        this.couponCode = couponCode;
        this.orderDate = orderDate;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.orderList = orderList;
        this.created = created;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Object getShipmentAddress() {
        return shipmentAddress;
    }

    public void setShipmentAddress(Object shipmentAddress) {
        this.shipmentAddress = shipmentAddress;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Object getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(Object couponCode) {
        this.couponCode = couponCode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Object getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Object totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderList> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderList> orderList) {
        this.orderList = orderList;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}