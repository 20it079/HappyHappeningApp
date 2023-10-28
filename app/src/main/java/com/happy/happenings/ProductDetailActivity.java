package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, vendorName, price, edit, delete, book, description;
    LinearLayout editLayout;

    SharedPreferences sp;
    ProgressDialog pd;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);

        getSupportActionBar().setTitle(sp.getString(ConstantUrl.PRODUCT_NAME, ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.product_detail_iv);
        name = findViewById(R.id.product_detail_name);
        vendorName = findViewById(R.id.product_detail_vendor_name);
        price = findViewById(R.id.product_detail_price);
        description = findViewById(R.id.product_detail_description);
        delete = findViewById(R.id.product_detail_delete);
        edit = findViewById(R.id.product_detail_edit);
        editLayout = findViewById(R.id.product_detail_edit_layout);
        book = findViewById(R.id.product_detail_book);

        name.setText(sp.getString(ConstantUrl.PRODUCT_NAME, ""));
        vendorName.setText(sp.getString(ConstantUrl.PRODUCT_VENDOR_NAME, ""));
        price.setText(getResources().getString(R.string.price_symbol) + sp.getString(ConstantUrl.PRODUCT_PRICE, ""));
        Picasso.get().load(sp.getString(ConstantUrl.PRODUCT_IMAGE, "")).placeholder(R.mipmap.ic_launcher).into(imageView);
        description.setText(sp.getString(ConstantUrl.PRODUCT_DESC, ""));

        if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("Admin")) {
            editLayout.setVisibility(View.GONE);
            book.setVisibility(View.GONE);
        } else if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("User")) {
            editLayout.setVisibility(View.GONE);
            book.setVisibility(View.VISIBLE);
        } else {
            editLayout.setVisibility(View.VISIBLE);
            book.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionDetector(ProductDetailActivity.this).isConnectingToInternet()) {
                    pd = new ProgressDialog(ProductDetailActivity.this);
                    pd.setMessage("Please Wait...");
                    pd.setCancelable(false);
                    pd.show();
                    deleteCategory();
                } else {
                    new ConnectionDetector(ProductDetailActivity.this).connectiondetect();
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString(ConstantUrl.PRODUCT_ADD_EDIT, "Edit").commit();
                new CommonMethod(ProductDetailActivity.this, AddProductActivity.class);
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonMethod(ProductDetailActivity.this, BookNowActivity.class);
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

    private void deleteCategory() {
        Call<AddCategoryData> call = apiInterface.deleteProductData(sp.getString(ConstantUrl.PRODUCT_ID, ""));
        call.enqueue(new Callback<AddCategoryData>() {
            @Override
            public void onResponse(Call<AddCategoryData> call, Response<AddCategoryData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        new CommonMethod(ProductDetailActivity.this, response.body().message);
                        onBackPressed();
                    } else {
                        new CommonMethod(ProductDetailActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(ProductDetailActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddCategoryData> call, Throwable t) {
                pd.dismiss();
                new CommonMethod(ProductDetailActivity.this, t.getMessage());
            }
        });
    }

}