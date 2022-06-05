package com.android.munozexpress;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Transactions");
        usersRef.keepSynced(true);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        TextView goPorder = view.findViewById(R.id.goPlaceOrder);
        TextView goCorder = view.findViewById(R.id.goCompleteOrder);
        TextView goCanOrder = view.findViewById(R.id.goCancelOrder);
        TextView goComOrder = view.findViewById(R.id.goConfirmOrder);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        usersRef.child("Pabili").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String cancel = datas.child("Cancelled").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if( Integer.parseInt(cancel) == 0 && Integer.parseInt(completed) == 0 && Integer.parseInt(ongoing) == 0){
                            goPorder.setClickable(true);
                            goPorder.setEnabled(true);
                            goPorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goPorder.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Placed_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            goComOrder.setClickable(true);
                            goComOrder.setEnabled(true);
                            goComOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goComOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Confirmed_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(cancel) == 1){
                            goCanOrder.setClickable(true);
                            goCanOrder.setEnabled(true);
                            goCanOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCanOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Cancelled_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(completed) == 1){
                            goCorder.setClickable(true);
                            goCorder.setEnabled(true);
                            goCorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Completed_Orders.class));
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        usersRef.child("Pahatid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String cancel = datas.child("Cancelled").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if( Integer.parseInt(cancel) == 0 && Integer.parseInt(completed) == 0 && Integer.parseInt(ongoing) == 0){
                            goPorder.setClickable(true);
                            goPorder.setEnabled(true);
                            goPorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goPorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Placed_Orders.class));
                                }
                            });
                        }else{
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            goComOrder.setClickable(true);
                            goComOrder.setEnabled(true);
                            goComOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goComOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Confirmed_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(cancel) == 1){
                            goCanOrder.setClickable(true);
                            goCanOrder.setEnabled(true);
                            goCanOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCanOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Cancelled_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(completed) == 1){
                            goCorder.setClickable(true);
                            goCorder.setEnabled(true);
                            goCorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Completed_Orders.class));
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
            usersRef.child("Pakuha").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();

                    String cancel = datas.child("Cancelled").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if( Integer.parseInt(cancel) == 0 && Integer.parseInt(completed) == 0 && Integer.parseInt(ongoing) == 0){
                            goPorder.setClickable(true);
                            goPorder.setEnabled(true);
                            goPorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goPorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Placed_Orders.class));
                                }
                            });
                        }else{
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            goComOrder.setClickable(true);
                            goComOrder.setEnabled(true);
                            goComOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goComOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Confirmed_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(cancel) == 1){
                            goCanOrder.setClickable(true);
                            goCanOrder.setEnabled(true);
                            goCanOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCanOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Cancelled_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(completed) == 1){
                            goCorder.setClickable(true);
                            goCorder.setEnabled(true);
                            goCorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Completed_Orders.class));
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
            usersRef.child("Pasundo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String databaseUid = datas.child("CustomerId").getValue().toString();
                    String cancel = datas.child("Cancelled").getValue().toString();
                    String completed = datas.child("Completed").getValue().toString();
                    String ongoing = datas.child("Ongoing").getValue().toString();
                    if(databaseUid.equals(uid)){
                        if( Integer.parseInt(cancel) == 0 && Integer.parseInt(completed) == 0 && Integer.parseInt(ongoing) == 0){
                            goPorder.setClickable(true);
                            goPorder.setEnabled(true);
                            goPorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goPorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Placed_Orders.class));
                                }
                            });
                        }else{
                        }
                        if(Integer.parseInt(ongoing) == 1){
                            goComOrder.setClickable(true);
                            goComOrder.setEnabled(true);
                            goComOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goComOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Confirmed_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(cancel) == 1){
                            goCanOrder.setClickable(true);
                            goCanOrder.setEnabled(true);
                            goCanOrder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCanOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Cancelled_Orders.class));
                                }
                            });
                        }
                        if(Integer.parseInt(completed) == 1){
                            goCorder.setClickable(true);
                            goCorder.setEnabled(true);
                            goCorder.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_chevron_right_24,0);
                            goCorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getActivity(),Completed_Orders.class));
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        goComOrder.setClickable(false);
        goPorder.setClickable(false);
        goCanOrder.setClickable(false);
        goCorder.setClickable(false);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_200));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new CountDownTimer(300, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("2")).commit();
                        getFragmentManager().beginTransaction().add(R.id.frame_layout,new TransactionFragment(),"2").commit();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }.start();

            }
        });
        return view;
    }
}