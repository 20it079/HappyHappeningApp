package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConstantUrl;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {

    EditText name;
    ImageView imageView;
    Button select, upload;
    int IMAGE_CODE = 1;
    int STORAGE_CODE = 123;
    Uri filePath;
    String sSelectedPath = "";
    ProgressDialog pd;
    ApiInterface apiInterface;
    String sImageSelect = "";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        requestStoragePermission();
        sp = getSharedPreferences(ConstantUrl.PREF,MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if(sp.getString(ConstantUrl.CATEGORY_ADD_EDIT,"").equalsIgnoreCase("Add")){
            getSupportActionBar().setTitle("Add Category");
        }
        else{
            getSupportActionBar().setTitle("Update Category");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.add_category_name);
        imageView = findViewById(R.id.add_category_iv);
        select = findViewById(R.id.add_category_select);
        upload = findViewById(R.id.add_category_button);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_CODE);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("Name Required");
                } else {
                    if(sp.getString(ConstantUrl.CATEGORY_ADD_EDIT,"").equalsIgnoreCase("Add")) {
                        if (sSelectedPath != "") {
                            pd = new ProgressDialog(AddCategoryActivity.this);
                            pd.setMessage("Please Wait...");
                            pd.setCancelable(false);
                            pd.show();
                            addCategory();
                        } else {
                            new CommonMethod(AddCategoryActivity.this, "Please Select Image");
                        }
                    }
                    else{
                        if(sImageSelect == ""){
                            pd = new ProgressDialog(AddCategoryActivity.this);
                            pd.setMessage("Please Wait...");
                            pd.setCancelable(false);
                            pd.show();
                            updateCategory();
                        }
                        else{
                            pd = new ProgressDialog(AddCategoryActivity.this);
                            pd.setMessage("Please Wait...");
                            pd.setCancelable(false);
                            pd.show();
                            updateCategoryImage();
                        }
                    }
                }

            }
        });

        if(sp.getString(ConstantUrl.CATEGORY_ADD_EDIT,"").equalsIgnoreCase("Add")){

        }
        else{
            name.setText(sp.getString(ConstantUrl.CATEGORY_NAME,""));
            Picasso.get().load(sp.getString(ConstantUrl.CATEGORY_IMAGE,"")).placeholder(R.mipmap.ic_launcher).into(imageView);
            imageView.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
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

    private void addCategory() {
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, name.getText().toString());

        File file = new File(sSelectedPath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        Call<AddCategoryData> call = apiInterface.addCategoryData(namePart, filePart);
        call.enqueue(new Callback<AddCategoryData>() {
            @Override
            public void onResponse(Call<AddCategoryData> call, Response<AddCategoryData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                        onBackPressed();
                    } else {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(AddCategoryActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddCategoryData> call, Throwable t) {
                pd.dismiss();
                new CommonMethod(AddCategoryActivity.this, t.getMessage());
            }
        });

    }

    private void updateCategory() {
        Call<AddCategoryData> call = apiInterface.updateCategoryData(sp.getString(ConstantUrl.CATEGORY_ID,""),name.getText().toString());
        call.enqueue(new Callback<AddCategoryData>() {
            @Override
            public void onResponse(Call<AddCategoryData> call, Response<AddCategoryData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                        onBackPressed();
                    } else {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(AddCategoryActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddCategoryData> call, Throwable t) {
                pd.dismiss();
                new CommonMethod(AddCategoryActivity.this, t.getMessage());
            }
        });

    }

    private void updateCategoryImage() {
        RequestBody idPart = RequestBody.create(MultipartBody.FORM, sp.getString(ConstantUrl.CATEGORY_ID,""));
        RequestBody namePart = RequestBody.create(MultipartBody.FORM, name.getText().toString());

        File file = new File(sSelectedPath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        Call<AddCategoryData> call = apiInterface.updateCategoryImageData(idPart,namePart, filePart);
        call.enqueue(new Callback<AddCategoryData>() {
            @Override
            public void onResponse(Call<AddCategoryData> call, Response<AddCategoryData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                        onBackPressed();
                    } else {
                        new CommonMethod(AddCategoryActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(AddCategoryActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddCategoryData> call, Throwable t) {
                pd.dismiss();
                new CommonMethod(AddCategoryActivity.this, t.getMessage());
            }
        });

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new CommonMethod(AddCategoryActivity.this, "Permission Granted");
            } else {
                new CommonMethod(AddCategoryActivity.this, "Permission Denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            imageView.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            filePath = data.getData();
            imageView.setImageURI(filePath);
            Log.d("RESPONSE_URI", String.valueOf(filePath));
            sSelectedPath = getImage(filePath);
            Log.d("RESPONSE_PATH", sSelectedPath);
            sImageSelect = "Yes";
        }
    }

    private String getImage(Uri uri) {
        if (uri != null) {
            String[] s_array = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, s_array, null, null, null);
            int id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = null;
            if (cursor.moveToFirst()) {
                do {
                    path = cursor.getString(id);
                }
                while (cursor.moveToNext());
                if (path != null) {
                    return path;
                }
            }
        }
        return "";
    }
}