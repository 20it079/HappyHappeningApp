package com.happy.happenings.RetrofitData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetEventData {
    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("Message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public List<GetEventResponse> response = null;

    public class GetEventResponse {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("eventDate")
        @Expose
        public String eventDate;
        @SerializedName("eventName")
        @Expose
        public String eventName;
    }
}
