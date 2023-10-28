package com.happy.happenings;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.happy.happenings.Utils.ConstantUrl;
import com.happy.happenings.Utils.JavaAPI;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity
{
	//EditText
	EditText edfogetemailId;

	//Button
	Button submit;

	//otp
	String otpNumber;

	//Random nuber
	final int min = 1111;
	final int max = 9999;
	final Random random = new Random();

	SharedPreferences sp;

	private String verificationId;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		getSupportActionBar().setTitle("Forgot Password");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mAuth = FirebaseAuth.getInstance();
		sp = getSharedPreferences(ConstantUrl.PREF, MODE_PRIVATE);
		edfogetemailId = findViewById(R.id.ed_forgot_email);

		submit = findViewById(R.id.reset_button);
		submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (edfogetemailId.getText().toString().trim().equals(""))
				{
					edfogetemailId.setError("Email Id Required");
					return;
				}
				else
				{
					String email = edfogetemailId.getText().toString().trim();

					//email store in sharedPReference
					sp.edit().putString(ConstantUrl.FORGOT_EMAIL_ID, String.valueOf(email)).commit();
					//random number
					/*                    int rNumber = random.nextInt(9999);*/
					/*int rNumber = random.nextInt((max - min) + 1) + min;

					//mail send
					new JavaAPI(ForgotPasswordActivity.this, email,
							"OTP Code For BookBox App", "Your OTP code is:" + "\t" +
							String.valueOf(rNumber)).execute();


					sp.edit().putString(ConstantUrl.OTP, String.valueOf(rNumber)).commit();*/
					sendVerificationCode("+91" + sp.getString(ConstantUrl.FORGOT_EMAIL_ID, ""));

					/*startActivity(new Intent(ForgotPasswordActivity.this, OTPGETACTIVITY.class));
					finish();*/

					final Dialog otpverifyDialog = new Dialog(ForgotPasswordActivity.this);
					otpverifyDialog.setContentView(R.layout.getotp_custom_dialog);

					//init editbox and textbox
					final EditText otpNumberED = (EditText) otpverifyDialog.findViewById(R.id.otp_code_getActivity);
					Button submitTV = (Button) otpverifyDialog.findViewById(R.id.verify_btn_getActivity);
					TextView resendOTP = (TextView) otpverifyDialog.findViewById(R.id.resend_getotpcode);
					TextView cancelTv = (TextView) otpverifyDialog.findViewById(R.id.close_alert_dialogbox_getActivity);
					otpverifyDialog.show();

					resendOTP.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							/*int rNumber = random.nextInt((max - min) + 1) + min;
							//mail send
							new JavaAPI(ForgotPasswordActivity.this, sp.getString(ConstantUrl.FORGOT_EMAIL_ID, ""), "OTP Code For BookBox App", "Your OTP code is:" + "\t" + String.valueOf(rNumber)).execute();
							sp.edit().putString(ConstantUrl.OTP, String.valueOf(rNumber)).commit();*/
							sendVerificationCode("+91" + sp.getString(ConstantUrl.FORGOT_EMAIL_ID, ""));
							Toast.makeText(ForgotPasswordActivity.this, "OTP resend", Toast.LENGTH_SHORT).show();
						}
					});
					submitTV.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							otpNumber = otpNumberED.getText().toString().trim();
							if (otpNumber.isEmpty())
							{
								otpNumberED.setError("Enter your otp");
							}
							else {
								verifyCode(otpNumberED.getText().toString());
							}
						}
					});
					cancelTv.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							otpverifyDialog.dismiss();
						}
					});
				}

			}
		});

	}

	private void verifyCode(String code) {
		//Log.d("RESPONSE", verificationId + "\n" + code);
		PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
		signInWithCredential(credential);
	}

	private void signInWithCredential(PhoneAuthCredential credential) {
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							startActivity(new Intent(ForgotPasswordActivity.this,ResetPasswordActivity.class));
							//Toast.makeText(ForgotPasswordActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            /*if (sp.getString(ConstantSp.OTP, "").equalsIgnoreCase(sOTP)) {
                                sp.edit().putString(ConstantSp.OTP_VARIFY, "1").commit();
                                pd = new ProgressDialog(ForgotPasswordActivity.this);
                                pd.setMessage("Please Wait...");
                                pd.setCancelable(false);
                                pd.show();
                                doLogin();
                            } else {
                                new ToastIntentClass(ForgotPasswordActivity.this, "OTP Does Not Match!!!");
                            }*/
						} else {
							Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	private void sendVerificationCode(String number) {
        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );*/
		//Log.d("RESPONSE", number);
		PhoneAuthProvider.getInstance().verifyPhoneNumber(
				number,
				60,
				TimeUnit.SECONDS,
				ForgotPasswordActivity.this,
				mCallBack
		);
	}

	private PhoneAuthProvider.OnVerificationStateChangedCallbacks
			mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

		@Override
		public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
			super.onCodeSent(s, forceResendingToken);
			verificationId = s;
		}

		@Override
		public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
			String code = phoneAuthCredential.getSmsCode();
			if (code != null) {
				char[] array = code.toCharArray();
				for (int i = 0; i < array.length; i++) {

				}
				//editText.setText(code);
				verifyCode(code);
			}
		}

		@Override
		public void onVerificationFailed(FirebaseException e) {
			Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if(id==android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
