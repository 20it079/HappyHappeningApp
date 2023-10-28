package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.happy.happenings.RetrofitData.AddBookNowData;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// implements PaymentResultListener
public class BookNowActivity extends AppCompatActivity implements PaymentResultListener {

    TextView productName, vendorName;
    Button submit;
    EditText qty, functionDate, address, remark, totalAmount, advanceAmount;

    ApiInterface apiInterface;
    SharedPreferences sp;

    ProgressDialog pd;

    int iPrice = 0;
    int productPrice = 0;
    int iPrice50 = 0;
    String sTransactionId = "";

    private static final String TAG = BookNowActivity.class.getSimpleName();

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);
        getSupportActionBar().setTitle("Book Now");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //Checkout.preload(getApplicationContext());

        productName = findViewById(R.id.book_now_product_name);
        vendorName = findViewById(R.id.book_now_vendor_name);
        submit = findViewById(R.id.book_now_continue);
        qty = findViewById(R.id.book_now_function_qty);
        functionDate = findViewById(R.id.book_now_function_date);
        address = findViewById(R.id.book_now_function_address);
        remark = findViewById(R.id.book_now_function_remark);
        advanceAmount = findViewById(R.id.book_now_function_advance);
        totalAmount = findViewById(R.id.book_now_function_total);

        productName.setText(sp.getString(ConstantUrl.PRODUCT_NAME, ""));
        vendorName.setText(sp.getString(ConstantUrl.PRODUCT_VENDOR_NAME, ""));
        productPrice = Integer.parseInt(sp.getString(ConstantUrl.PRODUCT_PRICE, ""));

        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateClick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                functionDate.setText(format.format(calendar.getTime()));

            }
        };

        functionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BookNowActivity.this, dateClick, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (qty.getText().toString().trim().equalsIgnoreCase("") || qty.getText().toString().equals("0")) {
                    submit.setText("Pay Now");
                    totalAmount.setText(getResources().getString(R.string.price_symbol) + "0");
                    advanceAmount.setText(getResources().getString(R.string.price_symbol) + "0");
                } else {
                    iPrice = productPrice * Integer.parseInt(s.toString());
                    iPrice50 = iPrice / 2;
                    totalAmount.setText(getResources().getString(R.string.price_symbol) + iPrice);
                    advanceAmount.setText(getResources().getString(R.string.price_symbol) + iPrice50);
                    submit.setText("Pay Now (" + getResources().getString(R.string.price_symbol) + iPrice50 + ")");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty.getText().toString().trim().equalsIgnoreCase("") || qty.getText().toString().equals("0")) {
                    qty.setError("Quantity Required");
                } else if (functionDate.getText().toString().trim().equalsIgnoreCase("")) {
                    functionDate.setError("Function Name Required");
                } else if (address.getText().toString().trim().equalsIgnoreCase("")) {
                    address.setError("Address Required");
                } else {
                    if (new ConnectionDetector(BookNowActivity.this).isConnectingToInternet()) {
                        startPayment();
                        /*pd = new ProgressDialog(BookNowActivity.this);
                        pd.setMessage("Please Wait...");
                        pd.setCancelable(false);
                        pd.show();
                        bookNowData();*/
                    } else {
                        new ConnectionDetector(BookNowActivity.this).connectiondetect();
                    }
                }
            }
        });

    }

    public void startPayment() {
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            //options.put("description", "Demoing Charges");
            options.put("description", sp.getString(ConstantUrl.PRODUCT_DESC, ""));
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", Double.parseDouble(String.valueOf(iPrice50)) * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", sp.getString(ConstantUrl.EMAIL, ""));
            preFill.put("contact", sp.getString(ConstantUrl.CONTACT, ""));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Log.d("RESPONSE", e.getMessage());
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            //Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            sTransactionId = razorpayPaymentID;
            if (new ConnectionDetector(BookNowActivity.this).isConnectingToInternet()) {
                pd = new ProgressDialog(BookNowActivity.this);
                pd.setMessage("Please Wait...");
                pd.setCancelable(false);
                pd.show();
                bookNowData();
            } else {
                new ConnectionDetector(BookNowActivity.this).connectiondetect();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.d("RESPONSE", "Payment Cancelled " + code + " " + response);
            //Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    private void bookNowData() {
        Call<AddBookNowData> call = apiInterface.addBookNowData(sp.getString(ConstantUrl.ID, ""), sp.getString(ConstantUrl.PRODUCT_VENDOR_ID, ""), sp.getString(ConstantUrl.PRODUCT_ID, ""), qty.getText().toString(), functionDate.getText().toString(), address.getText().toString(), remark.getText().toString(), String.valueOf(iPrice), String.valueOf(iPrice50), sTransactionId);
        call.enqueue(new Callback<AddBookNowData>() {
            @Override
            public void onResponse(Call<AddBookNowData> call, Response<AddBookNowData> response) {
                pd.dismiss();
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        new CommonMethod(BookNowActivity.this, response.body().message);
                        new CommonMethod(BookNowActivity.this, HomeActivity.class);
                    } else {
                        new CommonMethod(BookNowActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(BookNowActivity.this, "Server Error Code : " + " " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AddBookNowData> call, Throwable t) {
                pd.dismiss();
                new CommonMethod(BookNowActivity.this, t.getMessage());
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

}