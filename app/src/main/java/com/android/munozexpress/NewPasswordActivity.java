package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class NewPasswordActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        final Button resetBtn = findViewById(R.id.resetPasswordBtn);
        final EditText newPass = findViewById(R.id.newPassword);
        final EditText newRpass = findViewById(R.id.newRpassword);
        String phoneNum = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mAuth = FirebaseAuth.getInstance();
        reference =  FirebaseDatabase.getInstance().getReference("users-id");

        ProgressDialog pd = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);

        resetBtn.setOnClickListener(new View.OnClickListener() {
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
                                                    pd.dismiss();
                                                    reference.child(phoneNum).child("Password").setValue(newPassTxt);
                                                    startActivity(new Intent(NewPasswordActivity.this,MainActivity.class));
                                                    finish();
                                                    FirebaseAuth.getInstance().signOut();
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
    public void onClick(View v) {
        finish();
    }
}