package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.happy.happenings.RetrofitData.DeleteGalleryImageData;
import com.happy.happenings.RetrofitData.GetGalleryImageData;
import com.happy.happenings.SetGet.GalleryImageList;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<GalleryImageList> galleryImageLists;
    ImageAdapter galleryImageAdapter;

    ProgressDialog pd;
    ApiInterface apiService;
    SharedPreferences sp;

    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().setTitle("Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        sp = getSharedPreferences(ConstantUrl.PREF, Context.MODE_PRIVATE);

        add = findViewById(R.id.activity_gallery_add);

        if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("User")) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonMethod(GalleryActivity.this, AddGalleryImageActivity.class);
            }
        });

        recyclerView = findViewById(R.id.activity_gallery_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(GalleryActivity.this).isConnectingToInternet()) {
            pd = new ProgressDialog(GalleryActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
            getImageData();
        } else {
            new ConnectionDetector(GalleryActivity.this).connectiondetect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getImageData() {
        Call<GetGalleryImageData> call = apiService.getGalleryImageData(sp.getString(ConstantUrl.CATEGORY_ID, ""));
        call.enqueue(new Callback<GetGalleryImageData>() {
            @Override
            public void onResponse(Call<GetGalleryImageData> call, Response<GetGalleryImageData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        galleryImageLists = new ArrayList<>();
                        GetGalleryImageData getData = new GetGalleryImageData();
                        getData = response.body();
                        for (int i = 0; i < getData.response.size(); i++) {
                            GalleryImageList list = new GalleryImageList();
                            list.setId(getData.response.get(i).id);
                            list.setImage(getData.response.get(i).image);
                            galleryImageLists.add(list);
                        }
                        galleryImageAdapter = new ImageAdapter(GalleryActivity.this, galleryImageLists);
                        recyclerView.setAdapter(galleryImageAdapter);
                    } else {
                        new CommonMethod(GalleryActivity.this, response.body().message);
                    }
                } else {
                    pd = new ProgressDialog(GalleryActivity.this);
                    pd.setMessage("Please Wait...");
                    pd.setCancelable(false);
                    pd.show();
                    getImageData();
                }
            }

            @Override
            public void onFailure(Call<GetGalleryImageData> call, Throwable t) {
                pd.dismiss();
                //new ToastIntentClass(TopSellingActivity.this, getResources().getString(R.string.product_error_message));
            }
        });
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyHolder> {

        Context context;
        ArrayList<GalleryImageList> galleryImageLists;
        ProgressDialog pd;
        ApiInterface apiService;
        SharedPreferences sp;
        String sGalleryImageId;
        int galleryImagePosition;
        View view;

        ImageAdapter(Context context, ArrayList<GalleryImageList> galleryImageLists) {
            this.context = context;
            this.galleryImageLists = galleryImageLists;
            apiService = ApiClient.getClient().create(ApiInterface.class);
            sp = context.getSharedPreferences(ConstantUrl.PREF, Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public ImageAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin_gallery_image, parent, false);
            return new ImageAdapter.MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.MyHolder holder, int position) {
            if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("User")) {
                holder.deleteFloat.setVisibility(View.GONE);
            } else {
                holder.deleteFloat.setVisibility(View.VISIBLE);
            }
            Picasso.get().load(galleryImageLists.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.iv);

            holder.deleteFloat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sGalleryImageId = galleryImageLists.get(position).getId();
                    galleryImagePosition = position;
                    pd = new ProgressDialog(context);
                    pd.setMessage("Please Wait...");
                    pd.setCancelable(false);
                    pd.show();
                    deleteGalleryImageData();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.GALLER_IMAGE,galleryImageLists.get(position).getImage()).commit();
                    new CommonMethod(context,GalleryImageDetailActivity.class);
                }
            });

        }

        private void deleteGalleryImageData() {
            Call<DeleteGalleryImageData> call = apiService.deleteGalleryImageData(sGalleryImageId);
            call.enqueue(new Callback<DeleteGalleryImageData>() {
                @Override
                public void onResponse(Call<DeleteGalleryImageData> call, Response<DeleteGalleryImageData> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().status.equalsIgnoreCase("True")) {
                            new CommonMethod(context, response.body().message);
                            galleryImageLists.remove(galleryImagePosition);
                            galleryImageAdapter.notifyDataSetChanged();
                        } else {
                            new CommonMethod(context, response.body().message);
                        }
                    } else {
                        pd = new ProgressDialog(context);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        deleteGalleryImageData();
                    }
                }

                @Override
                public void onFailure(Call<DeleteGalleryImageData> call, Throwable t) {
                    pd.dismiss();
                    //new ToastIntentClass(TopSellingActivity.this, getResources().getString(R.string.product_error_message));
                }
            });
        }

        @Override
        public int getItemCount() {
            return galleryImageLists.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            FloatingActionButton deleteFloat;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.custom_admin_gallery_image_iv);
                deleteFloat = itemView.findViewById(R.id.custom_admin_gallery_image_delete);
            }
        }
    }

}