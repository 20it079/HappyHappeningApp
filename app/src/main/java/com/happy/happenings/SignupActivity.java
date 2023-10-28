package com.happy.happenings;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.happy.happenings.Utils.ApiClient;
import com.happy.happenings.Utils.ApiInterface;
import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.happy.happenings.Utils.MakeServiceCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, contact, password,address;
    RadioGroup gender;
    Spinner spinner;
    Button signup;

    String sGender, sCity;

    String[] cityArray = {"Ahmedabad", "Gandhinagar", "Vadodara", "Surat"};

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ApiInterface apiInterface;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        name = findViewById(R.id.json_signup_name);
        address = findViewById(R.id.json_signup_address);
        email = findViewById(R.id.json_signup_email);
        contact = findViewById(R.id.json_signup_contact);
        password = findViewById(R.id.json_signup_password);
        gender = findViewById(R.id.json_signup_gender);
        spinner = findViewById(R.id.json_signup_spinner);
        signup = findViewById(R.id.json_signup_button);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = gender.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(id);
                sGender = radioButton.getText().toString();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(SignupActivity.this, android.R.layout.simple_list_item_1, cityArray);
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
                    Toast.makeText(SignupActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().equalsIgnoreCase("")) {
                    address.setError("Address Required");
                } else {
                    if (new ConnectionDetector(SignupActivity.this).isConnectingToInternet()) {
                        new signupData().execute();
                    } else {
                        new ConnectionDetector(SignupActivity.this).connectiondetect();
                    }
                }
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
            pd = new ProgressDialog(SignupActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("type", "User");
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
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                    onBackPressed();
                } else {
                    new CommonMethod(SignupActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(SignupActivity.this, e.getMessage());
            }
        }
    }

}