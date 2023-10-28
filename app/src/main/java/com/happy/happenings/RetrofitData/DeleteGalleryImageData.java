package com.happy.happenings.RetrofitData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteGalleryImageData {

    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("Message")
    @Expose
    public String message;

}
