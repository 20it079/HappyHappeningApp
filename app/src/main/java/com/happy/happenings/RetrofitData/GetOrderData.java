package com.happy.happenings.RetrofitData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOrderData {

    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("Message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public List<GetOrderResponse> response = null;

    public class GetOrderResponse {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("userName")
        @Expose
        public String userName;
        @SerializedName("userContact")
        @Expose
        public String userContact;
        @SerializedName("vendorId")
        @Expose
        public String vendorId;
        @SerializedName("vendorName")
        @Expose
        public String vendorName;
        @SerializedName("vendorContact")
        @Expose
        public String vendorContact;
        @SerializedName("productId")
        @Expose
        public String productId;
        @SerializedName("productName")
        @Expose
        public String productName;
        @SerializedName("productPrice")
        @Expose
        public String productPrice;
        @SerializedName("productDesc")
        @Expose
        public String productDesc;
        @SerializedName("productImage")
        @Expose
        public String productImage;
        @SerializedName("qty")
        @Expose
        public String qty;
        @SerializedName("functionDate")
        @Expose
        public String functionDate;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("remark")
        @Expose
        public String remark;
        @SerializedName("totalAmount")
        @Expose
        public String totalAmount;
        @SerializedName("advanceAmount")
        @Expose
        public String advanceAmount;
        @SerializedName("transactionId")
        @Expose
        public String transactionId;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
    }
}
