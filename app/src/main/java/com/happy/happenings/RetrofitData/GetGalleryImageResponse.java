package com.happy.happenings.RetrofitData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGalleryImageResponse {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("categoryId")
    @Expose
    public String categoryId;
    @SerializedName("image")
    @Expose
    public String image;
}
