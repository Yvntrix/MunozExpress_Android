package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText pass = findViewById(R.id.passW);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final  Button forgotBtn  = findViewById(R.id.forgotBtn);
        String phoneNum = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        ProgressDialog pd = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String passTxt = pass.getText().toString();
                if(passTxt.isEmpty()){
                    pass.setError("Please enter your password!");
                    pass.requestFocus();
                }else{
                    databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final String getPass = snapshot.child(phoneNum).child("Password").getValue(String.class);
                            final String getEmail = snapshot.child(phoneNum).child("Email").getValue(String.class);
                            if(getPass.equals(passTxt)){
                                pd.show();
                                pd.setContentView(R.layout.progress_dialog);
                                firebaseAuth.signInWithEmailAndPassword(getEmail,getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            pd.dismiss();
                                            Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
                                            intent.putExtra(Intent.EXTRA_TEXT, phoneNum);
                                            finish();
                                            startActivity(intent);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                        }

                                    }
                                });

                            }else{
                                pass.setText("");
                                pass.setError("Wrong Password!");
                                pass.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignInActivity.this,ForgotPasswrodActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, phoneNum);
                finish();
                startActivity(intent);
            }
        });
    }
}