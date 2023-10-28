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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happy.happenings.RetrofitData.AddCategoryData;
import com.happy.happenings.RetrofitData.GetOrderData;
import com.happy.happenings.RetrofitData.GetProductData;
import com.happy.happenings.SetGet.OrderList;
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

public class OrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static ArrayList<OrderList> arrayList;
    OrderAdapter adapter;

    ApiInterface apiInterface;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setTitle("Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recyclerView = findViewById(R.id.order_recyclerview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (new ConnectionDetector(OrderActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(OrderActivity.this).connectiondetect();
        }
    }

    private void getData() {
        Call<GetOrderData> call = apiInterface.getOrderData(sp.getString(ConstantUrl.ID, ""), sp.getString(ConstantUrl.TYPE, ""));
        call.enqueue(new Callback<GetOrderData>() {
            @Override
            public void onResponse(Call<GetOrderData> call, Response<GetOrderData> response) {
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        arrayList = new ArrayList<>();
                        GetOrderData data = response.body();
                        for (int i = 0; i < data.response.size(); i++) {
                            OrderList list = new OrderList();
                            list.setId(data.response.get(i).id);
                            list.setUserName(data.response.get(i).userName);
                            list.setUserContact(data.response.get(i).userContact);
                            list.setVendorName(data.response.get(i).vendorName);
                            list.setVendorContact(data.response.get(i).vendorContact);
                            list.setPrductName(data.response.get(i).productName);
                            list.setProductPrice(data.response.get(i).productPrice);
                            list.setProductDesc(data.response.get(i).productDesc);
                            list.setProductImage(data.response.get(i).productImage);
                            list.setQty(data.response.get(i).qty);
                            list.setFunctionDate(data.response.get(i).functionDate);
                            list.setAddress(data.response.get(i).address);
                            list.setRemark(data.response.get(i).remark);
                            list.setTotalAmount(data.response.get(i).totalAmount);
                            list.setAdvanceAmount(data.response.get(i).advanceAmount);
                            list.setTransactionId(data.response.get(i).transactionId);
                            list.setCreated_date(data.response.get(i).createdDate);
                            arrayList.add(list);
                        }
                        adapter = new OrderAdapter(OrderActivity.this, arrayList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        new CommonMethod(OrderActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(OrderActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetOrderData> call, Throwable t) {
                new CommonMethod(OrderActivity.this, t.getMessage());
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

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyHolder> {
        Context context;
        ArrayList<OrderList> arrayList;
        String sId;
        int iPosition;
        ProgressDialog pd;

        public OrderAdapter(OrderActivity adminActivity, ArrayList<OrderList> arrayList) {
            this.context = adminActivity;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public OrderAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order, parent, false);
            return new OrderAdapter.MyHolder(view);
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView orderNo, name, vendorName, userName, totalPrice, advance, remaining, date, functionDate, orderView;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.custom_order_iv);
                orderNo = itemView.findViewById(R.id.custom_order_order_no);
                name = itemView.findViewById(R.id.custom_order_name);
                vendorName = itemView.findViewById(R.id.custom_order_vendor_name);
                userName = itemView.findViewById(R.id.custom_order_user_name);
                totalPrice = itemView.findViewById(R.id.custom_order_totalprice);
                advance = itemView.findViewById(R.id.custom_order_advance);
                remaining = itemView.findViewById(R.id.custom_order_remain);
                date = itemView.findViewById(R.id.custom_order_date);
                orderView = itemView.findViewById(R.id.custom_order_view);
                functionDate = itemView.findViewById(R.id.custom_order_function_date);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdapter.MyHolder holder, int position) {
            holder.orderNo.setText("Order No : " + arrayList.get(position).getId());
            holder.name.setText("Product : " + arrayList.get(position).getPrductName() + " (" + arrayList.get(position).getQty() + " Qty)");
            holder.vendorName.setText("Vendor Name : " + arrayList.get(position).getVendorName());
            holder.userName.setText("Customer Name : " + arrayList.get(position).getUserName());
            holder.totalPrice.setText("Total :\n" + context.getResources().getString(R.string.price_symbol) + arrayList.get(position).getTotalAmount());
            holder.advance.setText("Advance :\n" + context.getResources().getString(R.string.price_symbol) + arrayList.get(position).getAdvanceAmount());
            int iRemain = Integer.parseInt(arrayList.get(position).getTotalAmount()) - Integer.parseInt(arrayList.get(position).getAdvanceAmount());
            holder.remaining.setText("Remaining :\n" + context.getResources().getString(R.string.price_symbol) + iRemain);
            Picasso.get().load(arrayList.get(position).getProductImage()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);

            holder.date.setText(arrayList.get(position).getCreated_date());
            holder.functionDate.setText("Function Date : " + arrayList.get(position).getFunctionDate());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putString(ConstantUrl.ORDER_ID, arrayList.get(position).getId()).commit();
                    sp.edit().putString(ConstantUrl.ORDER_POSITION, String.valueOf(position)).commit();
                    sp.edit().putString(ConstantUrl.ORDER_PAGE, "").commit();
                    new CommonMethod(context, OrderDetailActivity.class);
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

    }
}