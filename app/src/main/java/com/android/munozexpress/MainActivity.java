package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.munozexpress.databinding.ActivityMainBinding;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
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
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    //viewbind
    private ActivityMainBinding binding;
    //resend
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    //
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //
    private String mVerificationId;
    //
    private static final String TAG = "MAIN_TAG";

    private FirebaseAuth firebaseAuth;

    private ProgressDialog pd;

    PinView pinFromUser;

    Dialog dialog;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLl.setVisibility(View.VISIBLE);
        binding.codeLl.setVisibility(View.GONE);

        dialog = new Dialog(this);

        pd = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle);
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);

        pinFromUser = findViewById(R.id.pinView);

        CardView signRider = findViewById(R.id.signRider);

        signRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RiderLogin.class));
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verficationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verficationId, forceResendingToken);
                Log.d(TAG, "OncodeSent" + mVerificationId);
                mVerificationId = verficationId;
                forceResendingToken = token;
                pd.dismiss();

                //hide pgone
                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Verification code sent . . .", Toast.LENGTH_SHORT).show();

                binding.codeSentDescription.setText("Enter the 6-digit code sent \nto +63 " + binding.phoneEt.getText().toString().trim());


            }
        };



        binding.phoneContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.phoneEt.getText().toString().trim();
                String full = "+63" + phone;
                if (phone.length() < 10) {
                    binding.phoneEt.setError("Please enter a valid phone number!");
                    binding.phoneEt.requestFocus();
                } else {
                    databaseReference.child("riders").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(full)){
                                binding.phoneEt.setText("");
                                warn();
                            }else{
                                databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(full)){
                                            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                            intent.putExtra(Intent.EXTRA_TEXT, full);
                                            finish();
                                            startActivity(intent);
                                        }else{
                                            startPhoneNumberVerification(full);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });

        binding.resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.phoneEt.getText().toString().trim();
                String full = "+63" + phone;
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    resendVerificationCode(full, forceResendingToken);
                }

            }
        });
        //verify shhizz
        binding.codeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = pinFromUser.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(MainActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });


    }


    private void startPhoneNumberVerification(String phone) {
        pd.show();
        pd.setContentView(R.layout.progress_dialog);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(15L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging in");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //succes sign in lods
                        pd.dismiss();
                        //go next page.
                        String phone = "+63" + binding.phoneEt.getText().toString().trim();
                        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                        intent.putExtra(Intent.EXTRA_TEXT, phone);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed lods
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void warn(){
        dialog.setContentView(R.layout.rider_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        CardView btnOk = dialog.findViewById(R.id.signRider);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RiderLogin.class));
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}