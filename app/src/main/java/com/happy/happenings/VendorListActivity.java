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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.happy.happenings.RetrofitData.DeleteUserData;
import com.happy.happenings.RetrofitData.GetUserData;
import com.happy.happenings.SetGet.UserList;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UserList> arrayList;
    VendorAdapter adapter;

    ApiInterface apiInterface;
    SharedPreferences sp;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);
        getSupportActionBar().setTitle("Vendor List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recyclerView = findViewById(R.id.vendor_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        add = findViewById(R.id.vendor_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonMethod(VendorListActivity.this, AddVendorActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(VendorListActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(VendorListActivity.this).connectiondetect();
        }
    }

    private void getData() {
        Call<GetUserData> call = apiInterface.getUserData("Vendor");
        call.enqueue(new Callback<GetUserData>() {
            @Override
            public void onResponse(Call<GetUserData> call, Response<GetUserData> response) {
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        arrayList = new ArrayList<>();
                        GetUserData data = response.body();
                        for (int i = 0; i < data.response.size(); i++) {
                            UserList list = new UserList();
                            list.setId(data.response.get(i).id);
                            list.setName(data.response.get(i).name);
                            list.setEmail(data.response.get(i).email);
                            list.setContact(data.response.get(i).contact);
                            list.setGender(data.response.get(i).gender);
                            list.setCity(data.response.get(i).city);
                            list.setAddress(data.response.get(i).address);
                            list.setType(data.response.get(i).type);
                            arrayList.add(list);
                        }
                        adapter = new VendorAdapter(VendorListActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        new CommonMethod(VendorListActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(VendorListActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetUserData> call, Throwable t) {
                new CommonMethod(VendorListActivity.this, t.getMessage());
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

    private class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyHolder> {
        Context context;
        ArrayList<UserList> arrayList;
        String sId;
        int iPosition;
        ProgressDialog pd;
        String sType;

        public VendorAdapter(VendorListActivity adminActivity, ArrayList<UserList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public VendorAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user, parent, false);
            return new VendorAdapter.MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            TextView name, email, contact, gender, city, delete, category;
            LinearLayout categoryLayout;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.custom_user_name);
                email = itemView.findViewById(R.id.custom_user_email);
                contact = itemView.findViewById(R.id.custom_user_contact);
                gender = itemView.findViewById(R.id.custom_user_gender);
                city = itemView.findViewById(R.id.custom_user_city);
                category = itemView.findViewById(R.id.custom_user_category);
                categoryLayout = itemView.findViewById(R.id.custom_user_category_layout);
                delete = itemView.findViewById(R.id.custom_user_delete);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull VendorAdapter.MyHolder holder, int position) {
            holder.name.setText(arrayList.get(position).getName());
            holder.email.setText(arrayList.get(position).getEmail());
            holder.contact.setText(arrayList.get(position).getContact());
            holder.gender.setText(arrayList.get(position).getGender());
            holder.category.setText(arrayList.get(position).getType());

            if(sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("Admin")){
                holder.delete.setVisibility(View.VISIBLE);
            }
            else{
                holder.delete.setVisibility(View.GONE);
            }

            if (arrayList.get(position).getType().equalsIgnoreCase("")) {
                holder.categoryLayout.setVisibility(View.GONE);
            } else {
                holder.categoryLayout.setVisibility(View.VISIBLE);
            }

            if (arrayList.get(position).getAddress().equalsIgnoreCase("")) {
                holder.city.setText(arrayList.get(position).getCity());
            } else {
                holder.city.setText(arrayList.get(position).getAddress() + "," + arrayList.get(position).getCity());
            }

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sId = arrayList.get(position).getId();
                    sType = arrayList.get(position).getType();
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.CATEGORY_NAME,arrayList.get(position).getType()).commit();
                    new CommonMethod(context,ProductActivity.class);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }


        private void deleteCategory() {
            Call<DeleteUserData> call = apiInterface.deleteUserData(sId,sType);
            call.enqueue(new Callback<DeleteUserData>() {
                @Override
                public void onResponse(Call<DeleteUserData> call, Response<DeleteUserData> response) {
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
                public void onFailure(Call<DeleteUserData> call, Throwable t) {
                    pd.dismiss();
                    new CommonMethod(context, t.getMessage());
                }
            });
        }

    }
}