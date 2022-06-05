package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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

public class RiderLogin extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);
        EditText riderPhone = findViewById(R.id.riderPhone);
        EditText riderPass = findViewById(R.id.riderPass);
        Button riderLoginBtn = findViewById(R.id.riderLoginBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        ProgressDialog pd = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);

        riderLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String riderPhoneTxt = "+63"+ riderPhone.getText().toString();
                String riderPassTxt = riderPass.getText().toString();
                if(riderPhoneTxt.isEmpty()){
                    riderPhone.setError("Phone number is required");
                    riderPhone.requestFocus();
                }else if(riderPassTxt.isEmpty()){
                    riderPass.setError("Password is required");
                    riderPass.requestFocus();
                }else{
                    databaseReference.child("riders").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String getPhone = snapshot.child(riderPhoneTxt).child("Phone").getValue(String.class);
                            String rphone = getPhone == null ? "" : getPhone;

                           if(rphone.equals("")){
                               riderPhone.setError("Phone Number not registered");
                               riderPhone.requestFocus();
                           }else{
                               String getPass = snapshot.child(rphone).child("Password").getValue(String.class);
                             if(!getPass.equals(riderPassTxt)){
                                 riderPass.setError("Password is wrong");
                                 riderPass.requestFocus();
                             }else{
                                 pd.show();
                                 pd.setContentView(R.layout.progress_dialog);
                                 String getEmail = snapshot.child(riderPhoneTxt).child("Email").getValue(String.class);
                                 firebaseAuth.signInWithEmailAndPassword(getEmail,getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                     @Override
                                     public void onComplete(@NonNull Task<AuthResult> task) {

                                         if (task.isSuccessful()) {
                                             pd.dismiss();
                                             Intent i = new Intent(getApplicationContext(),RiderHome.class);        // Specify any activity here e.g. home or splash or login etc
                                             i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                             i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                             i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                             i.putExtra("EXIT", true);
                                             startActivity(i);
                                             finish();
                                         } else {
                                             // If sign in fails, display a message to the user.
                                         }

                                     }
                                 });
                             }

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
    public void onClick(View v) {
        finish();
    }
}