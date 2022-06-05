package com.android.munozexpress;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RiderCompleted extends Fragment {

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ArrayList<OrderList> orders;
    FirebaseAuth mAuth;
    LinearLayout nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_completed, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        nodata = view.findViewById(R.id.nodata);
        recyclerView = view.findViewById(R.id.completedOrders);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this
                .getActivity()));

        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this.getActivity(),orders);
        recyclerView.setAdapter(orderAdapter);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Transactions");
        usersRef.keepSynced(true);

        usersRef.child("Pasundo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("AssignedTo").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)) {
                        if(Integer.parseInt(completed) == 1) {
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                            nodata.setVisibility(View.GONE);
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
                    String databaseUid = datas.child("AssignedTo").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)) {
                        if(Integer.parseInt(completed) == 1) {
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                            nodata.setVisibility(View.GONE);
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
                    String databaseUid = datas.child("AssignedTo").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)) {
                        if(Integer.parseInt(completed) == 1) {
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                            nodata.setVisibility(View.GONE);
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
                    String databaseUid = datas.child("AssignedTo").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    if(databaseUid.equals(uid)) {
                        if(Integer.parseInt(completed) == 1) {
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                            nodata.setVisibility(View.GONE);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_200));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new CountDownTimer(300, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        getFragmentManager().beginTransaction().detach(RiderCompleted.this).attach(RiderCompleted.this).commit();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }.start();

            }
        });


        return view;
    }
}