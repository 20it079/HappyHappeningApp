package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happy.happenings.Utils.ConstantUrl;
import com.squareup.picasso.Picasso;

public class OrderDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView orderNo,name,vendorName,vendorContact,vendorCall, userName,userContact,userCall,totalPrice,advance,remaining,date,functionDate,remark,transactionId;
    
    SharedPreferences sp;
    int position;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        sp = getSharedPreferences(ConstantUrl.PREF,MODE_PRIVATE);
        getSupportActionBar().setTitle("Order Id : "+sp.getString(ConstantUrl.ORDER_ID,""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.order_detail_iv);
        orderNo = findViewById(R.id.order_detail_order_no);
        name = findViewById(R.id.order_detail_name);
        vendorName = findViewById(R.id.order_detail_vendor_name);
        vendorContact = findViewById(R.id.order_detail_vendor_contact);
        vendorCall = findViewById(R.id.order_detail_vendor_call);
        userName = findViewById(R.id.order_detail_user_name);
        userContact = findViewById(R.id.order_detail_user_contact);
        userCall = findViewById(R.id.order_detail_user_call);
        totalPrice = findViewById(R.id.order_detail_totalprice);
        advance = findViewById(R.id.order_detail_advance);
        remaining = findViewById(R.id.order_detail_remain);
        date = findViewById(R.id.order_detail_date);
        remark = findViewById(R.id.order_detail_remark);
        transactionId = findViewById(R.id.order_detail_transaction_id);
        functionDate = findViewById(R.id.order_detail_function_date);
        
        position = Integer.parseInt(sp.getString(ConstantUrl.ORDER_POSITION,""));

        if(sp.getString(ConstantUrl.ORDER_PAGE,"").equalsIgnoreCase("")){
            orderNo.setText("Order No : "+OrderActivity.arrayList.get(position).getId());
            name.setText("Product : "+OrderActivity.arrayList.get(position).getPrductName()+" ("+OrderActivity.arrayList.get(position).getQty()+" Qty)");
            vendorName.setText("Vendor Name : "+OrderActivity.arrayList.get(position).getVendorName());
            vendorContact.setText("Contact No : "+OrderActivity.arrayList.get(position).getVendorContact());
            userName.setText("Customer Name : "+OrderActivity.arrayList.get(position).getUserName());
            userContact.setText("Contact No : "+OrderActivity.arrayList.get(position).getUserContact());
            totalPrice.setText("Total :\n"+getResources().getString(R.string.price_symbol) + OrderActivity.arrayList.get(position).getTotalAmount());
            advance.setText("Advance :\n"+getResources().getString(R.string.price_symbol) + OrderActivity.arrayList.get(position).getAdvanceAmount());
            int iRemain = Integer.parseInt(OrderActivity.arrayList.get(position).getTotalAmount())-Integer.parseInt(OrderActivity.arrayList.get(position).getAdvanceAmount());
            remaining.setText("Remaining :\n"+getResources().getString(R.string.price_symbol) + iRemain);
            Picasso.get().load(OrderActivity.arrayList.get(position).getProductImage()).placeholder(R.mipmap.ic_launcher).into(imageView);

            remark.setText("Remark :\n"+ OrderActivity.arrayList.get(position).getRemark());
            transactionId.setText("Transaction Id : "+OrderActivity.arrayList.get(position).getTransactionId());

            date.setText(OrderActivity.arrayList.get(position).getCreated_date());
            functionDate.setText("Function Date : "+OrderActivity.arrayList.get(position).getFunctionDate());

            userCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + OrderActivity.arrayList.get(position).getUserContact()));
                    startActivity(intent);
                }
            });

            vendorCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + OrderActivity.arrayList.get(position).getVendorContact()));
                    startActivity(intent);
                }
            });
        }
        else{
            orderNo.setText("Order No : "+MyOrderFragment.arrayList.get(position).getId());
            name.setText("Product : "+MyOrderFragment.arrayList.get(position).getPrductName()+" ("+MyOrderFragment.arrayList.get(position).getQty()+" Qty)");
            vendorName.setText("Vendor Name : "+MyOrderFragment.arrayList.get(position).getVendorName());
            vendorContact.setText("Contact No : "+MyOrderFragment.arrayList.get(position).getVendorContact());
            userName.setText("Customer Name : "+MyOrderFragment.arrayList.get(position).getUserName());
            userContact.setText("Contact No : "+MyOrderFragment.arrayList.get(position).getUserContact());
            totalPrice.setText("Total :\n"+getResources().getString(R.string.price_symbol) + MyOrderFragment.arrayList.get(position).getTotalAmount());
            advance.setText("Advance :\n"+getResources().getString(R.string.price_symbol) + MyOrderFragment.arrayList.get(position).getAdvanceAmount());
            int iRemain = Integer.parseInt(MyOrderFragment.arrayList.get(position).getTotalAmount())-Integer.parseInt(MyOrderFragment.arrayList.get(position).getAdvanceAmount());
            remaining.setText("Remaining :\n"+getResources().getString(R.string.price_symbol) + iRemain);
            Picasso.get().load(MyOrderFragment.arrayList.get(position).getProductImage()).placeholder(R.mipmap.ic_launcher).into(imageView);

            remark.setText("Remark :\n"+getResources().getString(R.string.price_symbol) + MyOrderFragment.arrayList.get(position).getRemark());
            transactionId.setText("Transaction Id : "+MyOrderFragment.arrayList.get(position).getTransactionId());

            date.setText(MyOrderFragment.arrayList.get(position).getCreated_date());
            functionDate.setText("Function Date : "+MyOrderFragment.arrayList.get(position).getFunctionDate());

            userCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + MyOrderFragment.arrayList.get(position).getUserContact()));
                    startActivity(intent);
                }
            });

            vendorCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + MyOrderFragment.arrayList.get(position).getVendorContact()));
                    startActivity(intent);
                }
            });
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
}