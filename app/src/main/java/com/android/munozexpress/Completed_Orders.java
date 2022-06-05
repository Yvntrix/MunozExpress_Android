package com.android.munozexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Completed_Orders extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    OrderAdapter orderAdapter;
    ArrayList<OrderList> orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_orders);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        recyclerView = findViewById(R.id.completedOrders);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this,orders);
        recyclerView.setAdapter(orderAdapter);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Transactions");
        usersRef.child("Pasundo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if(Integer.parseInt(completed) == 1){
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        usersRef.child("Pakuha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if(Integer.parseInt(completed) == 1){
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        usersRef.child("Pabili").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid) ){
                        if(Integer.parseInt(completed) == 1){
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        usersRef.child("Pahatid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if(Integer.parseInt(completed) == 1){
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void onClick(View v) {
        finish();
    }
}