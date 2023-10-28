package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.RetrofitData.GetCategoryData;
import com.happy.happenings.SetGet.CategoryList;
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

public class GalleryCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CategoryList> arrayList;
    CategoryAdapter adapter;

    ApiInterface apiInterface;
    SharedPreferences sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_category);
        getSupportActionBar().setTitle("Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        
        recyclerView = findViewById(R.id.gallery_category_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(GalleryCategoryActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(GalleryCategoryActivity.this).connectiondetect();
        }
    }

    private void getData() {
        Call<GetCategoryData> call = apiInterface.getCategoryData();
        call.enqueue(new Callback<GetCategoryData>() {
            @Override
            public void onResponse(Call<GetCategoryData> call, Response<GetCategoryData> response) {
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        arrayList = new ArrayList<>();
                        GetCategoryData data = response.body();
                        for (int i = 0; i < data.response.size(); i++) {
                            CategoryList list = new CategoryList();
                            list.setId(data.response.get(i).id);
                            list.setName(data.response.get(i).name);
                            list.setImage(data.response.get(i).image);
                            arrayList.add(list);
                        }
                        adapter = new CategoryAdapter(GalleryCategoryActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        new CommonMethod(GalleryCategoryActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(GalleryCategoryActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetCategoryData> call, Throwable t) {
                new CommonMethod(GalleryCategoryActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyHolder> {
        Context context;
        ArrayList<CategoryList> arrayList;
        String sId;
        int iPosition;
        ProgressDialog pd;

        public CategoryAdapter(GalleryCategoryActivity adminActivity, ArrayList<CategoryList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public CategoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category, parent, false);
            return new CategoryAdapter.MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView name, edit, delete;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.custom_category_iv);
                name = itemView.findViewById(R.id.custom_category_name);
                delete = itemView.findViewById(R.id.custom_category_delete);
                edit = itemView.findViewById(R.id.custom_category_edit);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.MyHolder holder, int position) {
            holder.name.setText(arrayList.get(position).getName());
            Picasso.get().load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);

            /*if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("Admin")) {
                holder.delete.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
            }*/

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sId = arrayList.get(position).getId();
                    iPosition = position;
                    if (new ConnectionDetector(context).isConnectingToInternet()) {
                        pd = new ProgressDialog(context);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        deleteCategory();
                    } else {
                        new ConnectionDetector(context).connectiondetect();
                    }
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.CATEGORY_ADD_EDIT, "Edit").commit();
                    sp.edit().putString(ConstantUrl.CATEGORY_ID, arrayList.get(position).getId()).commit();
                    sp.edit().putString(ConstantUrl.CATEGORY_NAME, arrayList.get(position).getName()).commit();
                    sp.edit().putString(ConstantUrl.CATEGORY_IMAGE, arrayList.get(position).getImage()).commit();
                    new CommonMethod(context, AddCategoryActivity.class);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.CATEGORY_ID, arrayList.get(position).getId()).commit();
                    new CommonMethod(context, GalleryActivity.class);
                    /*if(sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("User")) {
                        sp.edit().putString(ConstantUrl.CATEGORY_NAME, arrayList.get(position).getName()).commit();
                        new CommonMethod(context, ProductActivity.class);
                    }*/
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }


        private void deleteCategory() {
            Call<AddCategoryData> call = apiInterface.deleteCategoryData(sId);
            call.enqueue(new Callback<AddCategoryData>() {
                @Override
                public void onResponse(Call<AddCategoryData> call, Response<AddCategoryData> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().status.equalsIgnoreCase("True")) {
                            new CommonMethod(context, response.body().message);
                            arrayList.remove(iPosition);
                            adapter.notifyDataSetChanged();
                        } else {
                            new CommonMethod(context, response.body().message);
                        }
                    } else {
                        new CommonMethod(context, "Server Error Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<AddCategoryData> call, Throwable t) {
                    pd.dismiss();
                    new CommonMethod(context, t.getMessage());
                }
            });
        }

    }
}