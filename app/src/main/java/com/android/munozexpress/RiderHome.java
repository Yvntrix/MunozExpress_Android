package com.android.munozexpress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class RiderHome extends AppCompatActivity {
    MeowBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        bottomNavigation = findViewById(R.id.bot_nav2);
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_transaction));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_twotone_delivery_dining_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_round_person_24));

        final FragmentManager t = getSupportFragmentManager();
        final Fragment transac = new RiderTransactions();
        final Fragment order = new RiderOrders();
        final Fragment acc = new RiderAccount();

        t.beginTransaction().add(R.id.frame_layout2, transac ,"1").hide(transac).commit();
        t.beginTransaction().add(R.id.frame_layout2,order ,"2").commit();
        t.beginTransaction().add(R.id.frame_layout2, acc ,"3").hide(acc).commit();


        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case 1:
                        t.beginTransaction().hide(acc).commit();
                        t.beginTransaction().hide(t.findFragmentByTag("2")).commit();
                        t.beginTransaction().show(t.findFragmentByTag("1")).commit();
                        break;
                    case 2:
                        t.beginTransaction().hide(t.findFragmentByTag("1")).commit();
                        t.beginTransaction().hide(acc).commit();
                        if(t.findFragmentByTag("2") == null){
                            t.beginTransaction().add(R.id.frame_layout,order,"2").commit();
                        }else{ t.beginTransaction().show(t.findFragmentByTag("2")).commit();}
                        break;
                    case 3:

                        t.beginTransaction().hide(t.findFragmentByTag("2")).commit();
                        t.beginTransaction().hide(t.findFragmentByTag("1")).commit();
                        t.beginTransaction().show(acc).commit();
                        break;
                }
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

        bottomNavigation.show(2,true);
    }
}