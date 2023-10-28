package com.happy.happenings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.happy.happenings.Utils.MakeServiceCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button signup, login;
    TextView forgotPassword;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        signup = findViewById(R.id.login_signup);
        login = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.login_forgot);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().equalsIgnoreCase("")) {
                    email.setError("Email Id Required");
                } else if (password.getText().toString().trim().equalsIgnoreCase("")) {
                    password.setError("Password Required");
                } else {
                    if (new ConnectionDetector(LoginActivity.this).isConnectingToInternet()) {
                        new loginData().execute();
                    } else {
                        new ConnectionDetector(LoginActivity.this).connectiondetect();
                    }
                }
            }
        });

    }

    private class loginData extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("email", email.getText().toString());
            hashMap.put("password", password.getText().toString());
            return new MakeServiceCall().MakeServiceCall(ConstantUrl.URL + "login.php", MakeServiceCall.POST, hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("Status").equalsIgnoreCase("True")) {
                    new CommonMethod(LoginActivity.this, object.getString("Message"));
                    JSONArray array = object.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        sp.edit().putString(ConstantUrl.ID, jsonObject.getString("id")).commit();
                        sp.edit().putString(ConstantUrl.TYPE, jsonObject.getString("type")).commit();
                        sp.edit().putString(ConstantUrl.NAME, jsonObject.getString("name")).commit();
                        sp.edit().putString(ConstantUrl.EMAIL, jsonObject.getString("email")).commit();
                        sp.edit().putString(ConstantUrl.CONTACT, jsonObject.getString("contact")).commit();
                        sp.edit().putString(ConstantUrl.PASSWORD, jsonObject.getString("password")).commit();
                        sp.edit().putString(ConstantUrl.GENDER, jsonObject.getString("gender")).commit();
                        sp.edit().putString(ConstantUrl.CITY, jsonObject.getString("city")).commit();
                        sp.edit().putString(ConstantUrl.ADDRESS, jsonObject.getString("address")).commit();

                        if (jsonObject.getString("type").equalsIgnoreCase("User")) {
                            new CommonMethod(LoginActivity.this, HomeActivity.class);
                        } else {
                            new CommonMethod(LoginActivity.this, AdminActivity.class);
                        }
                    }
                } else {
                    new CommonMethod(LoginActivity.this, object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(LoginActivity.this, e.getMessage());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //onBackPressed();
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}