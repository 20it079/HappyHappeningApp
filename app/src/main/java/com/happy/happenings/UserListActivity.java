package com.happy.happenings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.happy.happenings.RetrofitData.DeleteUserData;
import com.happy.happenings.RetrofitData.GetUserData;
import com.happy.happenings.SetGet.UserList;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;

public class UserListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UserList> arrayList;
    UserAdapter adapter;

    ApiInterface apiInterface;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        getSupportActionBar().setTitle("User List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recyclerView = findViewById(R.id.user_list_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(UserListActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(UserListActivity.this).connectiondetect();
        }
    }

    private void getData() {
        Call<GetUserData> call = apiInterface.getUserData("User");
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
                            list.setType(data.response.get(i).type);
                            list.setName(data.response.get(i).name);
                            list.setEmail(data.response.get(i).email);
                            list.setContact(data.response.get(i).contact);
                            list.setGender(data.response.get(i).gender);
                            list.setCity(data.response.get(i).city);
                            list.setAddress(data.response.get(i).address);
                            arrayList.add(list);
                        }
                        adapter = new UserAdapter(UserListActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        new CommonMethod(UserListActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(UserListActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetUserData> call, Throwable t) {
                new CommonMethod(UserListActivity.this, t.getMessage());
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

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {
        Context context;
        ArrayList<UserList> arrayList;
        String sId;
        int iPosition;
        ProgressDialog pd;

        public UserAdapter(UserListActivity adminActivity, ArrayList<UserList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user, parent, false);
            return new MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            TextView name, email, contact, gender, city, delete;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.custom_user_name);
                email = itemView.findViewById(R.id.custom_user_email);
                contact = itemView.findViewById(R.id.custom_user_contact);
                gender = itemView.findViewById(R.id.custom_user_gender);
                city = itemView.findViewById(R.id.custom_user_city);
                delete = itemView.findViewById(R.id.custom_user_delete);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.name.setText(arrayList.get(position).getName());
            holder.email.setText(arrayList.get(position).getEmail());
            holder.contact.setText(arrayList.get(position).getContact());
            holder.gender.setText(arrayList.get(position).getGender());

            if(sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("Admin")){
                holder.delete.setVisibility(View.VISIBLE);
            }
            else{
                holder.delete.setVisibility(View.GONE);
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

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }


        private void deleteCategory() {
            Call<DeleteUserData> call = apiInterface.deleteUserData(sId,"User");
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