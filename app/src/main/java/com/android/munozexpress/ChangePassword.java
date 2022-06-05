package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth = FirebaseAuth.getInstance();
        reference =  FirebaseDatabase.getInstance().getReference("users-id");
        String phoneNum = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        dialog = new Dialog(this);
        EditText oldPass = findViewById(R.id.verifyPassword);
        Button checkPass = findViewById(R.id.checkPassBtn);
        LinearLayout oldLayout = findViewById(R.id.checkPass);

        EditText newPass = findViewById(R.id.newPassword);
        EditText newRpass = findViewById(R.id.newRpassword);
        Button resetPass = findViewById(R.id.resetPasswordBtn);
        LinearLayout resetLayout = findViewById(R.id.resetPassword);

        ProgressDialog pd = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);

        databaseReference.keepSynced(true);
        checkPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassTxt = oldPass.getText().toString().trim();

                if (oldPassTxt.isEmpty() ){
                    oldPass.setError("Cannot be Empty!");
                    oldPass.requestFocus();
                }else{

                    databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(phoneNum).child("Password").getValue(String.class).equals(oldPassTxt)){
                                pd.show();
                                pd.setContentView(R.layout.progress_dialog);
                                new CountDownTimer(400, 1000) {

                                    public void onTick(long millisUntilFinished) {

                                    }

                                    public void onFinish() {
                                        oldLayout.setVisibility(View.GONE);
                                        resetLayout.setVisibility(View.VISIBLE);
                                        pd.dismiss();
                                        resetPass.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String newPassTxt = newPass.getText().toString().trim();
                                                String newRpassTxt = newRpass.getText().toString().trim();

                                                if (newPassTxt.isEmpty() ){
                                                    newPass.setError("Cannot be Empty!");
                                                    newPass.requestFocus();
                                                }else if(newRpassTxt.isEmpty() ){
                                                    newRpass.setError("Cannot be Empty!");
                                                    newRpass.requestFocus();
                                                }else{
                                                    if (newPassTxt.equals(newRpassTxt)){
                                                        pd.show();
                                                        pd.setContentView(R.layout.progress_dialog);
                                                        databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String email = snapshot.child(phoneNum).child("Email").getValue(String.class);
                                                                String pass = snapshot.child(phoneNum).child("Password").getValue(String.class);

                                                                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                        user.updatePassword(newPassTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                reference.child(phoneNum).child("Password").setValue(newPassTxt);
                                                                                pd.dismiss();
                                                                                successDialog();
                                                                            }
                                                                        });

                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }else{
                                                        newRpass.setError("Password do not match !");
                                                        newRpass.requestFocus();

                                                    }

                                                }
                                            }
                                        });
                                    }

                                }.start();
                            }else{
                                oldPass.setError("Wrong Password");
                                oldPass.requestFocus();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    private void successDialog() {
        dialog.setContentView(R.layout.success_changed);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        Button btnOk = dialog.findViewById(R.id.btnOks);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    public void onClick(View v) {
        finish();
    }
}