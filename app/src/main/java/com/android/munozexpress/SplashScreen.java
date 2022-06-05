package com.android.munozexpress;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;

    public boolean foreGroundRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(NotificationService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        Context context = getApplicationContext();
        Intent intent = new Intent(this,NotificationService.class); // Build the intent for the service
        if(!foreGroundRunning()){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(intent);

            }else{
                context.startService(intent);
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (!isConnected(this)) {
            showInternetDialog();
        }else{
            if(user != null){
                if(user.getEmail().length()>3){
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = rootRef.child("riders");
                    usersRef.keepSynced(true);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                String email = datas.child("Email").getValue().toString();
                                if(email.equals(user.getEmail())){
                                    startActivity(new Intent(getApplicationContext(),RiderHome.class));
                                    finish();
                                }
                                else{
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference usersRef2 = rootRef.child("users");
                                    usersRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                                String email = datas.child("Email").getValue().toString();
                                                if(email.equals(user.getEmail())){
                                                    startActivity(new Intent(getApplicationContext(),HomePageActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }else{
                    String phone = user.getPhoneNumber();
                    databaseReference.child("users-id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(user.getPhoneNumber())){
                                Intent intent = new Intent(SplashScreen.this, NewPasswordActivity.class);
                                intent.putExtra(Intent.EXTRA_TEXT, phone);
                                finish();
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(SplashScreen.this, SignUpActivity.class);
                                intent.putExtra(Intent.EXTRA_TEXT, phone);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }else {
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }
        }


    }

    private void showInternetDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        Button btnTryAgain = dialog.findViewById(R.id.tryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected(SplashScreen.this)) {
                    showInternetDialog();
                } else {
                    startActivity(new Intent(getApplicationContext(),SplashScreen.class));
                    finish();
                }
            }
        });

        dialog.show();

    }
    private boolean isConnected(SplashScreen splashScreenActivaity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) splashScreenActivaity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

}
