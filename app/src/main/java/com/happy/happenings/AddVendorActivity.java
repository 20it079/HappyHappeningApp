package com.happy.happenings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.happy.happenings.RetrofitData.GetCategoryData;
import com.happy.happenings.SetGet.CategoryList;
import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.happy.happenings.Utils.MakeServiceCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVendorActivity extends AppCompatActivity {

    EditText name, email, contact, password, address;
    RadioGroup gender;
    Spinner spinner, category;
    Button signup;

    String sGender, sCity, sCategory;

    String[] cityArray = {"Ahmedabad", "Gandhinagar", "Vadodara", "Surat"};

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ApiInterface apiInterface;
    ProgressDialog pd;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);
        getSupportActionBar().setTitle("Add Vendor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        name = findViewById(R.id.add_vendor_name);
        email = findViewById(R.id.add_vendor_email);
        contact = findViewById(R.id.add_vendor_contact);
        password = findViewById(R.id.add_vendor_password);
        gender = findViewById(R.id.add_vendor_gender);
        address = findViewById(R.id.add_vendor_address);
        spinner = findViewById(R.id.add_vendor_spinner);
        category = findViewById(R.id.add_vendor_category);
        signup = findViewById(R.id.add_vendor_button);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = gender.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(id);
                sGender = radioButton.getText().toString();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(AddVendorActivity.this, android.R.layout.simple_list_item_1, cityArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sCity = cityArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("Name Required");
                } else if (email.getText().toString().trim().equalsIgnoreCase("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (contact.getText().toString().trim().equalsIgnoreCase("")) {
                    contact.setError("Contact No. Required");
                } else if (contact.getText().toString().length() < 10 || contact.getText().toString().length() > 10) {
                    contact.setError("Valid Contact No. Required");
                } else if (password.getText().toString().trim().equalsIgnoreCase("")) {
                    password.setError("Password Required");
                } else if (gender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(AddVendorActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().equalsIgnoreCase("")) {
                    address.setError("Address Required");
                } else {
                    if (new ConnectionDetector(AddVendorActivity.this).isConnectingToInternet()) {
                        new signupData().execute();
                    } else {
                        new ConnectionDetector(AddVendorActivity.this).connectiondetect();
                    }
                }
            }
        });

        if (new ConnectionDetector(AddVendorActivity.this).isConnectingToInternet()) {
            getData();
        } else {
            new ConnectionDetector(AddVendorActivity.this).connectiondetect();
        }

    }

    private void getData() {
        Call<GetCategoryData> call = apiInterface.getCategoryData();
        call.enqueue(new Callback<GetCategoryData>() {
            @Override
            public void onResponse(Call<GetCategoryData> call, Response<GetCategoryData> response) {
                if (response.code() == 200) {
                    if (response.body().status.equalsIgnoreCase("True")) {
                        arrayList = new ArrayList<>();
                        GetCategoryData data = response.body();
                        for (int i = 0; i < data.response.size(); i++) {
                            arrayList.add(data.response.get(i).name);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(AddVendorActivity.this, android.R.layout.simple_list_item_1, arrayList);
                        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                        category.setAdapter(adapter);

                        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                sCategory = arrayList.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        new CommonMethod(AddVendorActivity.this, response.body().message);
                    }
                } else {
                    new CommonMethod(AddVendorActivity.this, "Server Error Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GetCategoryData> call, Throwable t) {
                new CommonMethod(AddVendorActivity.this, t.getMessage());
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

    private class signupData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AddVendorActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", sCategory);
            hashMap.put("name", name.getText().toString());
            hashMap.put("email", email.getText().toString());
            hashMap.put("contact", contact.getText().toString());
            hashMap.put("password", password.getText().toString());
            hashMap.put("gender", sGender);
            hashMap.put("city", sCity);
            hashMap.put("address", address.getText().toString());
            return new MakeServiceCall().MakeServiceCall(ConstantUrl.URL + "signup.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("Status") == true) {
                    new CommonMethod(AddVendorActivity.this, object.getString("Message"));
                    onBackPressed();
                } else {
                    new CommonMethod(AddVendorActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(AddVendorActivity.this, e.getMessage());
            }
        }
    }

}