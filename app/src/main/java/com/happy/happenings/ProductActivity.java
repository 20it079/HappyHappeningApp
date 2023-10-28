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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.RetrofitData.GetCategoryData;
import com.happy.happenings.RetrofitData.GetProductData;
import com.happy.happenings.SetGet.CategoryList;
import com.happy.happenings.SetGet.ProductList;
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

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ProductList> arrayList;
    ProductAdapter adapter;

    ApiInterface apiInterface;
    SharedPreferences sp;

    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle("Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        add = findViewById(R.id.product_add);

        if(sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("Admin") || sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("User")){
            add.setVisibility(View.GONE);
        }
        else{
            add.setVisibility(View.VISIBLE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString(ConstantUrl.PRODUCT_ADD_EDIT, "Add").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_ID, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_ID, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_NAME, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_NAME, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_PRICE, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_DESC, "").commit();
                sp.edit().putString(ConstantUrl.PRODUCT_IMAGE, "").commit();
                new CommonMethod(ProductActivity.this, AddProductActivity.class);
            }
        });

        recyclerView = findViewById(R.id.product_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(ProductActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(ProductActivity.this).connectiondetect();
        }
    }

    private void getData() {
        Call<GetProductData> call = apiInterface.getProductData(sp.getString(ConstantUrl.ID, ""), sp.getString(ConstantUrl.TYPE, ""), sp.getString(ConstantUrl.CATEGORY_NAME, ""));
        call.enqueue(new Callback<GetProductData>() {
            @Override
            public void onResponse(Call<GetProductData> call, Response<GetProductData> response) {
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        arrayList = new ArrayList<>();
                        GetProductData data = response.body();
                        for (int i = 0; i < data.response.size(); i++) {
                            ProductList list = new ProductList();
                            list.setId(data.response.get(i).id);
                            list.setVendorId(data.response.get(i).vendorId);
                            list.setVendorName(data.response.get(i).vendorName);
                            list.setName(data.response.get(i).name);
                            list.setPrice(data.response.get(i).price);
                            list.setDesc(data.response.get(i).desc);
                            list.setImage(data.response.get(i).image);
                            arrayList.add(list);
                        }
                        adapter = new ProductAdapter(ProductActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        new CommonMethod(ProductActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(ProductActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetProductData> call, Throwable t) {
                new CommonMethod(ProductActivity.this, t.getMessage());
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

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyHolder> {
        Context context;
        ArrayList<ProductList> arrayList;
        String sId;
        int iPosition;
        ProgressDialog pd;

        public ProductAdapter(ProductActivity adminActivity, ArrayList<ProductList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public ProductAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product, parent, false);
            return new ProductAdapter.MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView name,vendorName, price, edit, delete, book;
            LinearLayout editLayout;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.custom_product_iv);
                name = itemView.findViewById(R.id.custom_product_name);
                vendorName = itemView.findViewById(R.id.custom_product_vendor_name);
                price = itemView.findViewById(R.id.custom_product_price);
                delete = itemView.findViewById(R.id.custom_product_delete);
                edit = itemView.findViewById(R.id.custom_product_edit);
                editLayout = itemView.findViewById(R.id.custom_product_edit_layout);
                book = itemView.findViewById(R.id.custom_product_book);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ProductAdapter.MyHolder holder, int position) {
            holder.name.setText(arrayList.get(position).getName());
            holder.vendorName.setText(arrayList.get(position).getVendorName());
            holder.price.setText(context.getResources().getString(R.string.price_symbol) + arrayList.get(position).getPrice());
            Picasso.get().load(arrayList.get(position).getImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

            if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("Admin")) {
                holder.editLayout.setVisibility(View.GONE);
                holder.book.setVisibility(View.GONE);
            } else if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("User")) {
                holder.editLayout.setVisibility(View.GONE);
                holder.book.setVisibility(View.VISIBLE);
            } else {
                holder.editLayout.setVisibility(View.VISIBLE);
                holder.book.setVisibility(View.GONE);
            }

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
                    sp.edit().putString(ConstantUrl.PRODUCT_ADD_EDIT, "Edit").commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_ID, arrayList.get(position).getId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_ID, arrayList.get(position).getVendorId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_NAME, arrayList.get(position).getVendorName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_DESC, arrayList.get(position).getDesc()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                    new CommonMethod(context, AddProductActivity.class);
                }
            });

            holder.book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.PRODUCT_ID, arrayList.get(position).getId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_ID, arrayList.get(position).getVendorId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_NAME, arrayList.get(position).getVendorName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_DESC, arrayList.get(position).getDesc()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                    new CommonMethod(context, BookNowActivity.class);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.PRODUCT_ID, arrayList.get(position).getId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_ID, arrayList.get(position).getVendorId()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_VENDOR_NAME, arrayList.get(position).getVendorName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_NAME, arrayList.get(position).getName()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_PRICE, arrayList.get(position).getPrice()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_DESC, arrayList.get(position).getDesc()).commit();
                    sp.edit().putString(ConstantUrl.PRODUCT_IMAGE, arrayList.get(position).getImage()).commit();
                    new CommonMethod(context, ProductDetailActivity.class);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        private void deleteCategory() {
            Call<AddCategoryData> call = apiInterface.deleteProductData(sId);
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