package com.happy.happenings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.happy.happenings.Utils.ConstantUrl;
import com.squareup.picasso.Picasso;

public class GalleryImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_detail);
        getSupportActionBar().hide();
        sp = getSharedPreferences(ConstantUrl.PREF,MODE_PRIVATE);
        imageView = findViewById(R.id.gallery_image_detail_image);

        Picasso.get().load(sp.getString(ConstantUrl.GALLER_IMAGE,"")).placeholder(R.mipmap.ic_launcher).into(imageView);

    }
}