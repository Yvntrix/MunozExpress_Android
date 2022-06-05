package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordCodeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    PinView pinFromUser;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_code);
        Bundle extras = getIntent().getExtras();
        String phoneNum = extras.getString("EXTRA_TEXT");
        String verificationCode = extras.getString("EXTRA_NUM");

        firebaseAuth = FirebaseAuth.getInstance();
        pinFromUser = findViewById(R.id.pinForgot);
        Button submitForgotBtn = findViewById(R.id.codeSubmitForgotBtn);
        TextView codeSentForgot = findViewById(R.id.codeSentForgot);
        codeSentForgot.setText("Enter the 6-digit code sent \nto " + phoneNum);

        submitForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinFromUser.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Cant empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(verificationCode != null){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,pinFromUser.getText().toString().trim());
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(ResetPasswordCodeActivity.this,NewPasswordActivity.class);
                                    intent.putExtra(Intent.EXTRA_TEXT, phoneNum);
                                    finish();
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "OTP IS NOT VALID", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });
    }
    public void onClick(View v) {
        finish();
    }
}