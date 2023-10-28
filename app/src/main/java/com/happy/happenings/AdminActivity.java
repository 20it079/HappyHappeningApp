package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happy.happenings.SetGet.AdminList;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConstantUrl;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<AdminList> arrayList;
    AdminAdapter adapter;

    String[] nameArray = {"Users", "Vendors", "Category","Gallery", "Products", "Order", "Logout"};
    int[] imageArray = {R.drawable.profile, R.drawable.manager, R.drawable.category,R.drawable.ic_menu_gallery, R.drawable.category, R.drawable.member_icon, R.drawable.logout};

    String[] vendorNameArray = {"Users", "Vendors", "Category","Gallery", "Products", "Order", "Profile", "Logout"};
    int[] vendorImageArray = {R.drawable.profile, R.drawable.manager, R.drawable.category,R.drawable.ic_menu_gallery, R.drawable.category, R.drawable.member_icon, R.drawable.profile, R.drawable.logout};

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        getSupportActionBar().setTitle(sp.getString(ConstantUrl.TYPE,""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.admin_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();
        if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("Admin")) {
            for (int i = 0; i < nameArray.length; i++) {
                AdminList list = new AdminList();
                list.setName(nameArray[i]);
                list.setImage(imageArray[i]);
                arrayList.add(list);
            }
        } else {
            for (int i = 0; i < vendorNameArray.length; i++) {
                AdminList list = new AdminList();
                list.setName(vendorNameArray[i]);
                list.setImage(vendorImageArray[i]);
                arrayList.add(list);
            }
        }
        adapter = new AdminAdapter(AdminActivity.this, arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.MyHolder> {
        Context context;
        ArrayList<AdminList> arrayList;

        public AdminAdapter(AdminActivity adminActivity, ArrayList<AdminList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public AdminAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin, parent, false);
            return new MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView name;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.custom_admin_iv);
                name = itemView.findViewById(R.id.custom_admin_name);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull AdminAdapter.MyHolder holder, int position) {
            holder.name.setText(arrayList.get(position).getName());
            holder.imageView.setImageResource(arrayList.get(position).getImage());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        new CommonMethod(AdminActivity.this, UserListActivity.class);
                    } else if (position == 1) {
                        new CommonMethod(AdminActivity.this, VendorListActivity.class);
                    } else if (position == 2) {
                        new CommonMethod(AdminActivity.this, CategoryActivity.class);
                    } else if (position == 3) {
                        new CommonMethod(AdminActivity.this, GalleryCategoryActivity.class);
                    } else if (position == 4) {
                        sp.edit().putString(ConstantUrl.CATEGORY_NAME, "").commit();
                        new CommonMethod(AdminActivity.this, ProductActivity.class);
                    } else if (position == 5) {
                        new CommonMethod(AdminActivity.this, OrderActivity.class);
                    } else if (position == 6) {
                        if (sp.getString(ConstantUrl.TYPE, "").equalsIgnoreCase("Admin")) {
                            sp.edit().clear().commit();
                            new CommonMethod(AdminActivity.this, LoginActivity.class);
                        } else {
                            FragmentManager manager = getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.admin_layout, new ProfileFragment()).addToBackStack("").commit();
                        }
                    } else if (position == 7) {
                        sp.edit().clear().commit();
                        new CommonMethod(AdminActivity.this, LoginActivity.class);
                    } else {

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

    }
}