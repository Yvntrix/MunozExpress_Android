package com.android.munozexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class HomePageActivity extends AppCompatActivity {

    MeowBottomNavigation bottomNavigation;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        setContentView(R.layout.activity_home_page);
        //
        bottomNavigation = findViewById(R.id.bot_nav);

        //
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_transaction));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_round_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_round_person_24));


        final FragmentManager t = getSupportFragmentManager();
        final Fragment home = new HomeFragment();
        final Fragment acc = new AccountFragment();
        final Fragment transac = new TransactionFragment();

        Fragment active = home;
        t.beginTransaction().add(R.id.frame_layout, acc ,"3").hide(acc).commit();
        t.beginTransaction().add(R.id.frame_layout, transac ,"2").hide(transac).commit();
        t.beginTransaction().add(R.id.frame_layout, home ,"1").commit();
        String uid = user.getUid();


     databaseReference.child("Transactions/Pabili").addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
           }
           @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               if(snapshot.child("Cancelled").getValue()!= null &&snapshot.child("Completed").getValue()!= null &&snapshot.child("Ongoing").getValue()!= null){
                   String duid = snapshot.child("CustomerId").getValue().toString();
                   String cancel = snapshot.child("Cancelled").getValue().toString();
                   String completed = snapshot.child("Completed").getValue().toString();
                   String ongoing = snapshot.child("Ongoing").getValue().toString();
                   if(uid.equals(duid)){
                       if(Integer.parseInt(cancel) == 1){
                           notif("Pabili has been Cancelled" , snapshot.child("CustomerName").getValue().toString() ,new Cancelled_Orders() ,2);
                       }
                       if(Integer.parseInt(ongoing) == 1){
                           notif("Pabili has been confirmed and is now ongoing" , snapshot.child("CustomerName").getValue().toString(),new Confirmed_Orders(),2);
                       }
                       if(Integer.parseInt(completed) == 1){
                           notif("Pabili has been Completed" , snapshot.child("CustomerName").getValue().toString(),new Completed_Orders(),2);
                       }

                   }
               }
                }
           @Override
           public void onChildRemoved(@NonNull DataSnapshot snapshot) {
           }
           @Override
           public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
           }
       });
       /////////
        databaseReference.child("Transactions/Pahatid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("Cancelled").getValue()!= null &&snapshot.child("Completed").getValue()!= null &&snapshot.child("Ongoing").getValue()!= null){
                    String duid = snapshot.child("CustomerId").getValue().toString();
                    String cancel = snapshot.child("Cancelled").getValue().toString();
                    String completed = snapshot.child("Completed").getValue().toString();
                    String ongoing = snapshot.child("Ongoing").getValue().toString();
                    if(uid.equals(duid)){
                        if(Integer.parseInt(cancel) == 1){
                            notif("Pahatid has been Cancelled" , snapshot.child("CustomerName").getValue().toString()  ,new Cancelled_Orders(),3);
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            notif("Pahatid has been confirmed and is now ongoing" , snapshot.child("CustomerName").getValue().toString(),new Confirmed_Orders(),3);
                        }
                        if(Integer.parseInt(completed) == 1){
                            notif("Pahatid has been Completed" , snapshot.child("CustomerName").getValue().toString() ,new Completed_Orders(),3);
                        }

                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ///////
        databaseReference.child("Transactions/Pakuha").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("Cancelled").getValue()!= null &&snapshot.child("Completed").getValue()!= null &&snapshot.child("Ongoing").getValue()!= null){
                    String duid = snapshot.child("CustomerId").getValue().toString();
                    String cancel = snapshot.child("Cancelled").getValue().toString();
                    String completed = snapshot.child("Completed").getValue().toString();
                    String ongoing = snapshot.child("Ongoing").getValue().toString();
                    if(uid.equals(duid)){
                        if(Integer.parseInt(cancel) == 1){
                            notif("Pakuha has been Cancelled" , snapshot.child("CustomerName").getValue().toString()  ,new Cancelled_Orders(),4);
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            notif("Pakuha has been confirmed and is now ongoing" , snapshot.child("CustomerName").getValue().toString(),new Confirmed_Orders(),4);
                        }
                        if(Integer.parseInt(completed) == 1){
                            notif("Pakuha has been Completed" , snapshot.child("CustomerName").getValue().toString() ,new Completed_Orders(),4);
                        }
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ///////
        databaseReference.child("Transactions/Pasundo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("Cancelled").getValue()!= null &&snapshot.child("Completed").getValue()!= null &&snapshot.child("Ongoing").getValue()!= null){
                    String duid = snapshot.child("CustomerId").getValue().toString();
                    String cancel = snapshot.child("Cancelled").getValue().toString();
                    String completed = snapshot.child("Completed").getValue().toString();
                    String ongoing = snapshot.child("Ongoing").getValue().toString();
                    if(uid.equals(duid)){
                        if(Integer.parseInt(cancel) == 1){
                            notif("Pasundo has been Cancelled" , snapshot.child("CustomerName").getValue().toString()  ,new Cancelled_Orders(),5);
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            notif("Pasundo has been confirmed and is now ongoing" , snapshot.child("CustomerName").getValue().toString(),new Confirmed_Orders(),5);
                        }
                        if(Integer.parseInt(completed) == 1){
                            notif("Pasundo has been Completed" , snapshot.child("CustomerName").getValue().toString() ,new Completed_Orders(),5);
                        }

                    }
                }

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

            }
        });

//        bottomNavigation.setCount(1,"10");
        bottomNavigation.show(2,true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case 1:
                        t.beginTransaction().hide(home).commit();
                        t.beginTransaction().hide(acc).commit();
                        t.beginTransaction().show(t.findFragmentByTag("2")).commit();
                        break;
                    case 2:
                        t.beginTransaction().hide(t.findFragmentByTag("2")).commit();
                        t.beginTransaction().hide(acc).commit();
                        if(t.findFragmentByTag("1") == null){
                        t.beginTransaction().add(R.id.frame_layout,home,"1").commit();
                        }else{ t.beginTransaction().show(home).commit();}
                        break;
                    case 3:

                        t.beginTransaction().hide(home).commit();
                        t.beginTransaction().hide(t.findFragmentByTag("2")).commit();
                        t.beginTransaction().show(acc).commit();
                        break;
                }
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });


    }
    private void notif(String stat, String name, Activity page , int id){
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH
        );

        Intent notificationIntent = new Intent(this,page.getClass());

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle("Hello, "+name)
                .setContentText("Your order "+stat)
                .setSmallIcon(R.drawable.ic_black)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(getApplicationContext()).notify(id,notification.build());


    }

}