package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ForgotPasswrodActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    //
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //
    private ProgressDialog pd;
    private String mVerificationId;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passwrod);
        EditText phoneF = findViewById(R.id.phoneNum);
        Button conBtnF = findViewById(R.id.forgotContinueBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setTitle("Sending Verification code to ");
        pd.setCanceledOnTouchOutside(false);
        String phoneNum = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        phoneF.setText(phoneNum.substring(3));

        conBtnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneF.getText().toString();
                String fullNum = "+63" + phone;
                if (TextUtils.isEmpty(phone)) {
                    phoneF.setError("Please enter your phone number!");
                    phoneF.requestFocus();
                }else{
                   if (phone.length() < 10){
                       phoneF.setError("Please enter a valid number!");
                       phoneF.requestFocus();
                   }else{
                       databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if(snapshot.hasChild(fullNum)){

                                   mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                       @Override
                                       public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                       }

                                       @Override
                                       public void onVerificationFailed(@NonNull FirebaseException e) {

                                       }

                                       @Override
                                       public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                           super.onCodeSent(s, forceResendingToken);
                                           mVerificationId = s;
                                           Intent intent = new Intent(ForgotPasswrodActivity.this, ResetPasswordCodeActivity.class);
                                           Bundle extras = new Bundle();
                                           extras.putString("EXTRA_TEXT",fullNum);
                                           extras.putString("EXTRA_NUM",s);
                                           intent.putExtras(extras);
                                           startActivity(intent);
                                           finish();

                                       }
                                   };

                                   startPhoneNumberVerification(fullNum);

                               }else{
                                   phoneF.setError("Phone Number is not registered!!");
                                   phoneF.requestFocus();
                               }
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                   }
                }
            }
        });
    }
    private void startPhoneNumberVerification(String phone) {
        pd.setMessage(phone);
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(15L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    public void onClick(View v) {
        finish();
    }
}