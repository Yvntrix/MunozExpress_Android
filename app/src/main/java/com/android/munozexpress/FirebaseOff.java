package com.android.munozexpress;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseOff extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
