package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dialog = new Dialog(this);
        final EditText fname = findViewById(R.id.fname);
        final EditText lname = findViewById(R.id.lname);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText rpassword = findViewById(R.id.rpassword);


        final Button register = findViewById(R.id.registerBtn);
        String phoneNum = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fnameTxt = fname.getText().toString();
                final String lnameTxt = lname.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String rpasswordTxt = rpassword.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                    email.setError("Please provide valid email!");
                    email.requestFocus();
                    return;
                }
                if(passwordTxt.length()<6){
                    password.setError("Minimum password length should be 6 characters!");
                    password.requestFocus();
                    return;
                }
                if (!passwordTxt.equals(rpasswordTxt)) {
                    rpassword.setError("Password does not Match!");
                    rpassword.requestFocus();
                }
                    if (fnameTxt.isEmpty() || lnameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty() || rpasswordTxt.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please Fill all fields", Toast.LENGTH_SHORT).show();
                }else{
                        openAlertDialog();
                    mAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                databaseReference.child("users-id").child(phoneNum).child("PhoneUid").setValue(uid);
                                databaseReference.child("users-id").child(phoneNum).child("Password").setValue(passwordTxt);
                                databaseReference.child("users-id").child(phoneNum).child("Email").setValue(emailTxt);

                                mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("PhoneUid").setValue(uid);
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("FirstName").setValue(fnameTxt);
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("LastName").setValue(lnameTxt);
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("Email").setValue(emailTxt);
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("Phone").setValue(phoneNum);
                                        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).child("LoyalPoints").setValue(0);
                                        startActivity(new Intent(SignUpActivity.this, HomePageActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                dialog.dismiss();
                                email.setError("Email ALready Registered");
                                email.requestFocus();

                            }

                        }
                    });

                }

            }
        });


    }
    private void openAlertDialog(){
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}