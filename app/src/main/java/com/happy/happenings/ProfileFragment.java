package com.happy.happenings;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.happy.happenings.Utils.CommonMethod;
import com.happy.happenings.Utils.ConnectionDetector;
import com.happy.happenings.Utils.ConstantUrl;
import com.happy.happenings.Utils.MakeServiceCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    SharedPreferences sp;
    EditText name,email,contact,password,address;
    RadioGroup gender;
    RadioButton male,female,transgender;
    Spinner spinner;
    Button submit,editProfile;
    TextView category;
    LinearLayout categoryLayout;

    String[] cityArray = {"Ahmedabad","Vadodara","Surat"};
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String sGneder,sCity;
    Button logout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sp = getActivity().getSharedPreferences(ConstantUrl.PREF,MODE_PRIVATE);

        logout = view.findViewById(R.id.profile_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sp.edit().remove(ConstantUrl.ID).commit();
                sp.edit().clear().commit();
                new CommonMethod(getActivity(),LoginActivity.class);
            }
        });

        name = view.findViewById(R.id.profile_name);
        email = view.findViewById(R.id.profile_email);
        contact = view.findViewById(R.id.profile_contact);
        password = view.findViewById(R.id.profile_password);
        gender = view.findViewById(R.id.profile_gender);
        spinner = view.findViewById(R.id.profile_city);
        submit = view.findViewById(R.id.profile_submit);
        editProfile = view.findViewById(R.id.profile_edit_profile);

        address = view.findViewById(R.id.profile_address);
        category = view.findViewById(R.id.profile_category);
        categoryLayout = view.findViewById(R.id.profile_category_layout);

        male = view.findViewById(R.id.profile_male);
        female = view.findViewById(R.id.profile_female);
        transgender = view.findViewById(R.id.profile_transgender);

        name.setText(sp.getString(ConstantUrl.NAME,""));
        email.setText(sp.getString(ConstantUrl.EMAIL,""));
        contact.setText(sp.getString(ConstantUrl.CONTACT,""));
        password.setText(sp.getString(ConstantUrl.PASSWORD,""));
        category.setText(sp.getString(ConstantUrl.TYPE,""));
        if(sp.getString(ConstantUrl.TYPE,"").equalsIgnoreCase("User")){
            categoryLayout.setVisibility(View.GONE);
        }
        else{
            categoryLayout.setVisibility(View.VISIBLE);
        }
        address.setText(sp.getString(ConstantUrl.ADDRESS,""));
        sGneder = sp.getString(ConstantUrl.GENDER,"");
        if(sGneder.equalsIgnoreCase("Male")){
            male.setChecked(true);
        }
        else if(sGneder.equalsIgnoreCase("Female")){
            female.setChecked(true);
        }
        else if(sGneder.equalsIgnoreCase("Transgender")){
            transgender.setChecked(true);
        }
        else{

        }

        sCity = sp.getString(ConstantUrl.CITY,"");

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = gender.getCheckedRadioButtonId();
                RadioButton rb = view.findViewById(id);
                sGneder = rb.getText().toString();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,cityArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);

        for(int i=0;i<cityArray.length;i++){
            if(cityArray[i].equalsIgnoreCase(sCity)){
                spinner.setSelection(i);
                break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sCity = cityArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        name.setEnabled(false);
        email.setEnabled(false);
        contact.setEnabled(false);
        password.setEnabled(false);
        address.setEnabled(false);
        male.setEnabled(false);
        female.setEnabled(false);
        transgender.setEnabled(false);
        spinner.setEnabled(false);
        editProfile.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setEnabled(true);
                email.setEnabled(true);
                contact.setEnabled(true);
                password.setEnabled(true);
                address.setEnabled(true);
                male.setEnabled(true);
                female.setEnabled(true);
                transgender.setEnabled(true);
                spinner.setEnabled(true);
                editProfile.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().trim().equalsIgnoreCase("")){
                    name.setError("Name Required");
                }
                else if(email.getText().toString().trim().equalsIgnoreCase("")){
                    email.setError("Email Id Required");
                }
                else if(!email.getText().toString().trim().matches(emailPattern)){
                    email.setError("Valid Email Id Required");
                }
                else if(contact.getText().toString().trim().equalsIgnoreCase("")){
                    contact.setError("Contact No. Required");
                }
                else if(contact.getText().toString().trim().length()<10){
                    contact.setError("Valid Contact No. Required");
                }
                else if(password.getText().toString().trim().equalsIgnoreCase("")){
                    password.setError("Password Required");
                }
                else if(gender.getCheckedRadioButtonId()==-1){
                    new CommonMethod(getActivity(),"Please Select Gender");
                }
                else if(address.getText().toString().trim().equalsIgnoreCase("")){
                    address.setError("Address Required");
                }
                else{
                    if(new ConnectionDetector(getActivity()).isConnectingToInternet()){
                        new updateData().execute();
                    }
                    else{
                        new ConnectionDetector(getActivity()).connectiondetect();
                    }
                }
            }
        });
        return view;
    }

    private class updateData extends AsyncTask<String,String,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("name",name.getText().toString());
            hashMap.put("email",email.getText().toString());
            hashMap.put("contact",contact.getText().toString());
            hashMap.put("password",password.getText().toString());
            hashMap.put("gender",sGneder);
            hashMap.put("address",address.getText().toString());
            hashMap.put("city",sCity);
            hashMap.put("userId",sp.getString(ConstantUrl.ID,""));
            return new MakeServiceCall().MakeServiceCall(ConstantUrl.URL+"updateProfile.php", MakeServiceCall.POST,hashMap);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if(object.getBoolean("Status")==true){
                    new CommonMethod(getActivity(),object.getString("Message"));
                    name.setEnabled(false);
                    email.setEnabled(false);
                    contact.setEnabled(false);
                    password.setEnabled(false);
                    address.setEnabled(false);
                    male.setEnabled(false);
                    female.setEnabled(false);
                    transgender.setEnabled(false);
                    spinner.setEnabled(false);
                    editProfile.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);

                    sp.edit().putString(ConstantUrl.NAME,name.getText().toString()).commit();
                    sp.edit().putString(ConstantUrl.EMAIL,email.getText().toString()).commit();
                    sp.edit().putString(ConstantUrl.CONTACT,contact.getText().toString()).commit();
                    sp.edit().putString(ConstantUrl.PASSWORD,password.getText().toString()).commit();
                    sp.edit().putString(ConstantUrl.GENDER,sGneder).commit();
                    sp.edit().putString(ConstantUrl.CITY,sCity).commit();
                    sp.edit().putString(ConstantUrl.ADDRESS,address.getText().toString()).commit();

                }
                else{
                    new CommonMethod(getActivity(),object.getString("Message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new CommonMethod(getActivity(),e.getMessage());
            }
        }
    }
}