package com.happy.happenings.Utils;

import com.happy.happenings.RetrofitData.AddBookNowData;
import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.RetrofitData.AddEventData;
import com.happy.happenings.RetrofitData.AddGalleryImageData;
import com.happy.happenings.RetrofitData.DeleteGalleryImageData;
import com.happy.happenings.RetrofitData.DeleteUserData;
import com.happy.happenings.RetrofitData.GetCategoryData;
import com.happy.happenings.RetrofitData.GetEventData;
import com.happy.happenings.RetrofitData.GetGalleryImageData;
import com.happy.happenings.RetrofitData.GetOrderData;
import com.happy.happenings.RetrofitData.GetProductData;
import com.happy.happenings.RetrofitData.GetUserData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @GET("getCategory.php")
    Call<GetCategoryData> getCategoryData();

    @Multipart
    @POST("addCategory.php")
    Call<AddCategoryData> addCategoryData(@Part("name") RequestBody name, @Part MultipartBody.Part pdf);

    @Multipart
    @POST("updateCategoryImage.php")
    Call<AddCategoryData> updateCategoryImageData(@Part("id") RequestBody id, @Part("name") RequestBody name, @Part MultipartBody.Part pdf);

    @FormUrlEncoded
    @POST("updateCategory.php")
    Call<AddCategoryData> updateCategoryData(@Field("id") String id, @Field("name") String name);

    @FormUrlEncoded
    @POST("deleteCategory.php")
    Call<AddCategoryData> deleteCategoryData(@Field("id") String id);

    @FormUrlEncoded
    @POST("getUser.php")
    Call<GetUserData> getUserData(@Field("type") String type);

    @FormUrlEncoded
    @POST("deleteUser.php")
    Call<DeleteUserData> deleteUserData(@Field("id") String id, @Field("type") String type);

    @FormUrlEncoded
    @POST("getProduct.php")
    Call<GetProductData> getProductData(@Field("vendorId") String vendorId, @Field("type") String type, @Field("categoryName") String categoryId);

    @Multipart
    @POST("addProduct.php")
    Call<AddCategoryData> addProductData(@Part("vendorId") RequestBody vendorId,@Part("categoryName") RequestBody categoryName,@Part("name") RequestBody name,@Part("price") RequestBody price, @Part("desc") RequestBody desc, @Part MultipartBody.Part pdf);

    @Multipart
    @POST("updateProductImage.php")
    Call<AddCategoryData> updateProductImageData(@Part("vendorId") RequestBody vendorId,@Part("id") RequestBody id, @Part("name") RequestBody name, @Part("price") RequestBody price, @Part("desc") RequestBody desc, @Part MultipartBody.Part pdf);

    @FormUrlEncoded
    @POST("updateProduct.php")
    Call<AddCategoryData> updateProductData(@Field("id") String id, @Field("name") String name, @Field("price") String price, @Field("desc") String desc);

    @FormUrlEncoded
    @POST("deleteProduct.php")
    Call<AddCategoryData> deleteProductData(@Field("id") String id);

    @FormUrlEncoded
    @POST("addBookNow.php")
    Call<AddBookNowData> addBookNowData(
            @Field("userId") String userId,
            @Field("vendorId") String vendorId,
            @Field("productId") String productId,
            @Field("qty") String qty,
            @Field("functionDate") String functionDate,
            @Field("address") String address,
            @Field("remark") String remark,
            @Field("totalAmount") String totalAmount,
            @Field("advanceAmount") String advanceAmount,
            @Field("transactionId") String transactionId
            );

    @FormUrlEncoded
    @POST("getOrder.php")
    Call<GetOrderData> getOrderData(@Field("vendorId") String vendorId, @Field("type") String type);

    @FormUrlEncoded
    @POST("getGallery.php")
    Call<GetGalleryImageData> getGalleryImageData(@Field("categoryId") String categoryId);

    @Multipart
    @POST("addGallery.php")
    Call<AddGalleryImageData> addGalleryImageData(@Part MultipartBody.Part pdf,@Part("categoryId") RequestBody categoryId);

    @FormUrlEncoded
    @POST("deleteGallery.php")
    Call<DeleteGalleryImageData> deleteGalleryImageData(@Field("id") String id);

    @FormUrlEncoded
    @POST("getEvent.php")
    Call<GetEventData> getEventData(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("addEvent.php")
    Call<AddEventData> addEventData(@Field("userId") String userId,@Field("eventName") String eventName,@Field("eventDate") String eventDate);

}
