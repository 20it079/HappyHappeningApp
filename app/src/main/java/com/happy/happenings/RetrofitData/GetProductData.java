package com.happy.happenings.RetrofitData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetProductData {
    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("Message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public List<GetProductResponse> response = null;

    public class GetProductResponse {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("vendorId")
        @Expose
        public String vendorId;
        @SerializedName("vendorName")
        @Expose
        public String vendorName;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("image")
        @Expose
        public String image;
    }
}