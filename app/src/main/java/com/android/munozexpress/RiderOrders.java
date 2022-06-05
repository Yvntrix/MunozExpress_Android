package com.android.munozexpress;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RiderOrders extends Fragment {

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    ArrayList<OrderList> orders;
    FirebaseAuth mAuth;
    Switch olSwitch;
    LinearLayout nodata;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rider_orders, container, false);
        olSwitch = view.findViewById(R.id.onlineSwitch);
        recyclerView = view.findViewById(R.id.availableOrders);

        nodata = view.findViewById(R.id.nodata);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this
        .getActivity()));

        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter(this.getActivity(),orders);
        recyclerView.setAdapter(orderAdapter);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Transactions");
        usersRef.keepSynced(true);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
                    rootRef.child("riders-id/"+ uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if ( Integer.parseInt(snapshot.child("Online").getValue().toString()) == 1){
                                olSwitch.setChecked(true);
                            }else{
                                olSwitch.setChecked(false);
                            }
                            olSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if(b){
                                           rootRef.child("riders-id/"+uid).child("Online").setValue(1);
                                        }else{
                                            rootRef.child("riders-id/"+uid).child("Online").setValue(0);
                                        }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        usersRef.child("Pasundo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String ongoing= datas.child("Ongoing").getValue().toString();
                    String duid = datas.child("AssignedTo").getValue().toString();

                        if(Integer.parseInt(ongoing) == 1 && uid.equals(duid) ) {
                            OrderList orderList = datas.getValue(OrderList.class);
                            orders.add(orderList);
                            nodata.setVisibility(View.GONE);
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
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    String duid = datas.child("AssignedTo").getValue().toString();

                    if(Integer.parseInt(ongoing) == 1 && uid.equals(duid) ) {
                        OrderList orderList = datas.getValue(OrderList.class);
                        orders.add(orderList);
                        nodata.setVisibility(View.GONE);

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
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    String duid = datas.child("AssignedTo").getValue().toString();

                    if(Integer.parseInt(ongoing) == 1 && uid.equals(duid) ) {
                        OrderList orderList = datas.getValue(OrderList.class);
                        orders.add(orderList);
                        nodata.setVisibility(View.GONE);
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
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    String duid = datas.child("AssignedTo").getValue().toString();

                    if(Integer.parseInt(ongoing) == 1 && uid.equals(duid) ) {
                        OrderList orderList = datas.getValue(OrderList.class);
                        orders.add(orderList);
                        nodata.setVisibility(View.GONE);
                    }

                }
                orderAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_200));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new CountDownTimer(300, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("2")).commit();
                        getFragmentManager().beginTransaction().add(R.id.frame_layout2,new RiderOrders(),"2").commit();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }.start();

            }
        });

        return view;
    }
}