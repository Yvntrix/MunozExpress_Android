package com.android.munozexpress;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;


public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    StorageReference storageReference;
    Boolean nopic ;
    Dialog dialog;
    Button btnOk ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        TextView userName = view.findViewById(R.id.userName);
        TextView point = view.findViewById(R.id.points);
        nopic = true;
        Dialog   pd = new Dialog(getActivity());
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);
        CardView pakuha = view.findViewById(R.id.pakuha);
        CardView pabili = view.findViewById(R.id.pabili);
        CardView pahatid = view.findViewById(R.id.pahatid);
        CardView pasundo = view.findViewById(R.id.pasundo);
        CardView openFb = view.findViewById(R.id.openFb);
        dialog = new Dialog(getActivity());
        storageReference  = FirebaseStorage.getInstance().getReference();
            pd.show();
        storageReference.child("pictures/"+ user.getUid()).getMetadata().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                nopic = false;
                pd.dismiss();
            }
        });

      openFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String YourPageURL = "https://www.facebook.com/groups/636893377110376";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YourPageURL));
                startActivity(browserIntent);
            }
        });

        pakuha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nopic){
                    warn();
                }else{
                    startActivity(new Intent(getActivity(),PakuhaActivity.class));
                }

            }
        });
        pabili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nopic){
                    warn();
                }else{
                    startActivity(new Intent(getActivity(),PabiliActivity.class));
                }

            }
        });

        pahatid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nopic){
                    warn();
                }
                else{
                    startActivity(new Intent(getActivity(),PahatidActivity.class));
                }

            }
        });

        pasundo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nopic){
                    warn();
                }else{
                    startActivity(new Intent(getActivity(),PasundoActivity.class));
                }

            }
        });

        databaseReference.child("users/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               userName.setText("Hello, " + snapshot.child("FirstName").getValue(String.class) +" "+ snapshot.child("LastName").getValue(String.class));
               point.setText(snapshot.child("LoyalPoints").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void warn(){
        dialog.setContentView(R.layout.nopic_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        btnOk = dialog.findViewById(R.id.btnOks);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}